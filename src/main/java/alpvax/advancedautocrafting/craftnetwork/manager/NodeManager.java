package alpvax.advancedautocrafting.craftnetwork.manager;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.ISimpleCraftNetworkNodeFactory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class NodeManager implements INBTSerializable<CompoundNBT> {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  static {
    MinecraftForge.EVENT_BUS.addListener(NodeManager::tickWorld);
  }
  private static final Multimap<IWorldReader, ChunkPos> dirtyManagers = HashMultimap.create();

  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
    NodeManager manager;
    public Provider(Chunk chunk) {
      manager = new NodeManager(chunk.getWorld(), chunk.getPos());
    }
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return Capabilities.NODE_MANAGER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> manager));
    }
    @Override
    public CompoundNBT serializeNBT() {
      return manager.serializeNBT();
    }
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      manager.deserializeNBT(nbt);
    }
  }

  private static void tickWorld(TickEvent.WorldTickEvent event) {
    if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
      IWorldReader world = event.world;
      dirtyManagers.get(world).stream()
          .map(pos -> get(world, pos))
          .forEach(NodeManager::updateNodes);
    }
  }

  // *************** NODE CREATION ***************

  public static Map<ResourceLocation, ISimpleCraftNetworkNodeFactory> NON_TILE_NODES = new HashMap<>(); //TODO: Reimplement non-tileEntity nodes
  private static Optional<INetworkNode> createNode(IWorldReader world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    TileEntity tile = world.getTileEntity(pos);
    if (tile != null) {
      LazyOptional<INetworkNode> cap = tile.getCapability(Capabilities.NODE_CAPABILITY);
      if (cap.isPresent()) {
        return  Optional.of(cap.orElseThrow(() -> new NullPointerException("Impossible condition reached: null cap passes isPresent check")));
      }
    }
    Block block = state.getBlock();
    ResourceLocation name = block.getRegistryName();
    INetworkNode node = null;
    if (NON_TILE_NODES.containsKey(name)) {
      node = NON_TILE_NODES.get(name).createNode(state, world, pos);
    } else if (block instanceof ISimpleCraftNetworkNodeFactory) {
      node = ((ISimpleCraftNetworkNodeFactory) block).createNode(state, world, pos);
    }
    return Optional.ofNullable(node);
  }

  // *************** HELPER GETTERS ***************
  @Nonnull
  public static NodeManager get(INetworkNode node) {
    return get(node.getWorld(), node.getPos());
  }
  @Nonnull
  public static NodeManager get(IWorldReader world, BlockPos pos) {
    return get(world.getChunk(pos));
  }
  @Nonnull
  public static NodeManager get(IWorldReader world, ChunkPos pos) {
    return get(world.getChunk(pos.x, pos.z));
  }
  @Nonnull
  public static NodeManager get(IChunk chunk) {
    LazyOptional<NodeManager> cap = chunk instanceof ICapabilityProvider
                                        ? ((ICapabilityProvider)chunk).getCapability(Capabilities.NODE_MANAGER_CAPABILITY)
                                        : LazyOptional.empty();
    return cap.orElseThrow(() -> new NullPointerException(
        String.format("Attempting to get NodeManager from Chunk {%s} failed: Capability doesn't exist!", chunk)
    ));
  }

  public static Optional<INetworkNode> getNodeAt(IWorldReader world, BlockPos pos) {
    return get(world, pos).getNodeAt(pos);
  }
  public static Optional<Set<CraftNetwork>> getNetworksAt(IWorldReader world, BlockPos pos) {
    return get(world, pos).getNetworksAt(pos);
  }

  public static void markNodeDirty(@Nonnull INetworkNode node) {
    get(node.getWorld(), node.getPos()).markDirty(node);
  }

  @Nullable
  private static NetworkNodeEntry getNodeEntry(@Nonnull IWorldReader world, @Nonnull BlockPos pos) {
    return get(world, pos).nodes.get(pos);
  }

  public static void updateNodeAt(IWorldReader world, BlockPos pos) {
    get(world, pos).updateNode(pos);
  }


  // *************** INSTANCE FUNCTIONALITY ***************
  private final IWorldReader world;
  private final ChunkPos chunkPos;
  private final Map<BlockPos, NetworkNodeEntry> nodes = new HashMap<>();

  public NodeManager(IWorldReader world, ChunkPos pos) {
    this.world = world;
    chunkPos = pos;
  }

  protected final void validatePos(@Nonnull BlockPos pos, @Nullable String message) {
    ChunkPos cpos = new ChunkPos(pos);
    if (!cpos.equals(chunkPos)) {
      throw new IndexOutOfBoundsException(String.format(
          "Attempted to %s from the wrong chunk! Requested pos: %s (Chunk: %s); Requested from Chunk: %s",
          message == null ? "get node" : message, pos, cpos, chunkPos
      ));
    }
  }

  @Nullable
  private NetworkNodeEntry getEntry(@Nonnull BlockPos pos) {
    NetworkNodeEntry entry = getEntryRaw(pos, false);
    if (entry != null && entry.isDirty()) {
      createNode(world, pos).ifPresent(entry::setNode);
    }
    return entry;
  }

  private NetworkNodeEntry getEntryRaw(@Nonnull BlockPos pos, boolean create) {
    validatePos(pos, "get instance of node");
    return create
        ? nodes.computeIfAbsent(pos, NetworkNodeEntry::new)
        : nodes.get(pos);
  }
  //private final Map<BlockPos, INetworkNode> nodes = new HashMap<>();
  //private final Multimap<BlockPos, CraftNetwork> connectedNetworks = HashMultimap.create();

  /**
   * Get all the nodes handled by this manager.
   * For just the nodes, use `.keySet` on the return value.
   * @return an Unmodifiable Multimap (multiple values) of all nodes, with their connected networks.
   * Entry will have a single value `null` if the node is not currently connected to any network.
   */
  @Nonnull
  public Multimap<INetworkNode, CraftNetwork> getNodes() {
    Multimap<INetworkNode, CraftNetwork> map = HashMultimap.create();
    nodes.values().stream()
        .peek(entry -> {
          if (entry.isDirty()) {
            entry.setNode(createNode(world, entry.pos).orElse(null));
          }
        })
        .filter(NetworkNodeEntry::isNode)
        .forEach(e -> {
          Set<CraftNetwork> networks = e.getNetworks();
          if (networks.isEmpty()) {
            // Force add null to the map for nodes which don't have networks yet.
            map.put(e.getNode(), null);
          }
          else {
            map.putAll(e.getNode(), e.getNetworks());
          }
        });
    return Multimaps.unmodifiableMultimap(map);
  }

  @Nonnull
  public Optional<INetworkNode> getNodeAt(BlockPos pos) {
    return Optional.ofNullable(getEntry(pos)).map(NetworkNodeEntry::getNode);
  }

  @Nonnull
  public Optional<Set<CraftNetwork>> getNetworksAt(BlockPos pos) {
    return Optional.ofNullable(getEntry(pos)).map(NetworkNodeEntry::getNetworks);
  }

  public void markDirty(@Nonnull INetworkNode node) {
    Optional.ofNullable(getEntryRaw(node.getPos(), false)).ifPresent(NetworkNodeEntry::markDirty);
  }

  private EnumMap<Direction, NetworkNodeEntry> getNeighbours(BlockPos pos) {
    EnumMap<Direction, NetworkNodeEntry> map = new EnumMap<>(Direction.class);
    for (Direction d : ALL_DIRECTIONS) {
      BlockPos adj = pos.offset(d);
      NetworkNodeEntry entry = getNodeEntry(world, adj);
      if (entry != null && entry.isNode() && entry.isConnected(d.getOpposite())) {
        map.put(d, entry);
      }
    }
    return map;
  }

  protected void setNode(@Nonnull BlockPos pos, @Nullable INetworkNode node, boolean updateNeighbors) {
    validatePos(pos, "set node");
    NetworkNodeEntry entry = getEntryRaw(pos, node != null);
    if (entry != null) {
      if (node == null) {
        nodes.remove(pos);
      }
      if (entry.getNode() != node) {
        entry.setNode(node);
        if (updateNeighbors) {
          getNeighbours(pos).forEach((d, e) -> e.markDirty());
        }
      }
    }
  }

  private void updateNodes() {
    nodes.values().stream()
        .filter(NetworkNodeEntry::isDirty)
        .forEach(entry -> updateNode(entry.pos));
    dirtyManagers.remove(world, chunkPos);
  }

  /**
   * Call to add a simple node, or to add the node already at that position (i.e. a tileentity)
   * @param pos the pos to query for creating the node.
   */
  public void updateNode(BlockPos pos) {
    setNode(pos, createNode(world, pos).orElse(null), true);
  }

  public void addNode(@Nonnull INetworkNode node) {
    setNode(node.getPos(), node, true);
  }

  public void removeNode(@Nonnull INetworkNode node) {
    removeNode(node.getPos());
  }
  public void removeNode(@Nonnull BlockPos pos) {
    setNode(pos, null, true);
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT nbt = new CompoundNBT();
    ListNBT positions = new ListNBT();
    for (Map.Entry<BlockPos, NetworkNodeEntry> e : nodes.entrySet()) {
      BlockPos pos = e.getKey();
      NetworkNodeEntry entry = e.getValue();
      if (entry.isNode()) { //Only save valid nodes
        CompoundNBT tag = new CompoundNBT();
        tag.putIntArray("position", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        //TODO: what else?
        positions.add(tag);
      } else {
        LogManager.getLogger().warn("Attempting to save empty node at {}. How did this happen?!", pos);
        //How did this happen?!
      }
    }
    nbt.put("nodes", positions);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    ListNBT positions = nbt.getList("nodes", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < positions.size(); i++) {
      CompoundNBT tag = positions.getCompound(i);
      int[] p = tag.getIntArray("position");
      if (p.length != 3) {
        throw new IndexOutOfBoundsException(String.format(
            "Attempting to load invalid position: %s. Number of values must be 3, actual: %d",
            Arrays.toString(p), p.length
        ));
      }
      BlockPos pos = new BlockPos(p[0], p[1], p[2]);
      getEntryRaw(pos, true);
      dirtyManagers.put(world, chunkPos);
    }
  }
}
