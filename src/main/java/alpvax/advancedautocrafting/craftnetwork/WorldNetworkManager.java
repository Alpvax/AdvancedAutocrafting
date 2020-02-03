package alpvax.advancedautocrafting.craftnetwork;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class WorldNetworkManager implements INetworkManager {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  private World world;
  private Multimap<ChunkPos, UUID> chunkToIDs = HashMultimap.create();
  private Map<UUID, CraftNetwork> networks = new HashMap<>();

  WorldNetworkManager() {
    // Default capability function
  }

  public WorldNetworkManager(World world) {
    this();
    this.world = world;
  }

  @Override
  public void addNode(BlockPos pos, @Nullable INetworkNode node) {
    Set<CraftNetwork> adjacent = getSurroundingNetworks(pos);
    CraftNetwork network;
    if(adjacent.size() < 1) {
      network = createNetwork();
    } else if(adjacent.size() < 2) {
      network = adjacent.iterator().next();
    } else {
      network = mergeNetworks(adjacent);
    }
    network.addNode(pos, node);
    ChunkPos cpos = new ChunkPos(pos);
    if(!chunkToIDs.containsEntry(cpos, network.id)) {
      chunkToIDs.put(cpos, network.id);
    }
  }

  @Override
  public void removeNode(BlockPos pos) {
    getNetworkAt(pos).ifPresent((network) -> {
      network.removeNode(pos);
      Set<CraftNetwork> adjacent = getSurroundingNetworks(pos);
      if(adjacent.size() < 1) {
        removeNetwork(network.id);
      } else {
        splitNetworks(pos);
        ChunkPos cpos = new ChunkPos(pos);
        if(network.inChunk(cpos)) {
          chunkToIDs.remove(cpos, network.id);
        }
      }
    });
  }

  private Set<CraftNetwork> getSurroundingNetworks(BlockPos pos) {
    return Arrays.stream(ALL_DIRECTIONS)
        .map(pos::offset)
        .map(this::getNetworkAt)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }

  private Optional<CraftNetwork> getNetworkAt(BlockPos pos) {
    Collection<UUID> chunkNetworks = chunkToIDs.get(new ChunkPos(pos));
    if(chunkNetworks.size() > 0) {
      return chunkNetworks.stream().map(networks::get).filter(net -> net.contains(pos)).findFirst();
    }
    return Optional.empty();
  }

  private CraftNetwork mergeNetworks(Collection<CraftNetwork> craftNetworks) {
    //TODO: Merge and remove dead networks
    return craftNetworks.iterator().next();//TODO: actually merge the networks
  }

  private void splitNetworks(BlockPos pos) {
    //TODO: Split and create networks
  }

  private CraftNetwork createNetwork() {
    UUID id = UUID.randomUUID();
    CraftNetwork network = new CraftNetwork(id);
    networks.put(id, network);
    return network;
  }

  private void removeNetwork(UUID networkID) {
    Set<Map.Entry<ChunkPos, UUID>> toRemove = chunkToIDs.entries().stream().filter(e -> e.getValue().equals(networkID)).collect(Collectors.toSet());
    toRemove.forEach(e -> {
      chunkToIDs.remove(e.getKey(), e.getValue());
    });
    networks.remove(networkID).invalidate();
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT nbt = new CompoundNBT();
    ListNBT list = new ListNBT();
    networks.forEach((uuid, craftNetwork) -> {
      CompoundNBT tag = craftNetwork.serializeNBT();
      tag.putUniqueId("id", uuid);
      list.add(tag);
    });
    nbt.put("networks", list);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    ListNBT list = nbt.getList("networks", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i < list.size(); i++) {
      CompoundNBT tag = list.getCompound(i);
      CraftNetwork network = networks.computeIfAbsent(tag.getUniqueId("id"), uuid -> new CraftNetwork(uuid));
      network.deserializeNBT(tag);
    }
  }

  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
    private final World world;
    private final WorldNetworkManager manager;

    public Provider(World world) {
      this.world = world;
      manager = new WorldNetworkManager(world);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return LazyOptional.of(() -> manager).cast();
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
}
