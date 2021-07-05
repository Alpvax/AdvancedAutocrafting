package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.api.util.WorldUtil;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.container.util.ContainerBlockHolder;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerTileEntity extends TileEntity  implements INamedContainerProvider {
  private INetworkNode network;// = new SimpleNetworkNode((ServerWorld) world, pos);
  private final LazyOptional<INetworkNode> networkCapability = LazyOptional.of(() -> network);
  public ControllerTileEntity() {
    super(AABlocks.TileTypes.CONTROLLER.get());
  }

  @Override
  public void setLevelAndPosition(@Nonnull World level, @Nonnull BlockPos pos) {
    super.setLevelAndPosition(level, pos);
    if (!level.isClientSide) {
      network = new SimpleNetworkNode(pos, level.dimension().location());
    }
  }

  /*@Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT nbt = super.getUpdateTag();
    ListNBT list = new ListNBT();
    for (ProxyBlockSource p : getNodePositions()) {
      list.add(NBTUtil.writeBlockPos(p.getBlockPos()));
    }
    nbt.put("nodePositions", list);
    return nbt;
  }

  @Override
  public void handleUpdateTag(BlockState state, CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    nodePositions.clear();
    ListNBT list = tag.getList("nodePositions", Constants.NBT.TAG_COMPOUND);
    list.forEach(t -> nodePositions.add(new ProxyBlockSource(world, NBTUtil.readBlockPos((CompoundNBT)t))));
  }*/

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
    return AABlocks.CONTROLLER.get().getName();
  }

  @Nullable
  @Override
  public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
    return new ControllerContainer(id, playerInventory, this);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == Capabilities.NODE_CAPABILITY ? networkCapability.cast() : super.getCapability(cap, side);
  }


  //TODO: replace with more full fledged network handling
  private final List<INetworkNode> nodes = new ArrayList<>();
  public  List<INetworkNode> updateAdjacentNetwork() {
    nodes.clear();
    if (level != null && !level.isClientSide) {
      nodes.add(network);
      for (Direction d : Direction.values()) {
        WorldUtil.getInstance().getBlockCapability(Capabilities.NODE_CAPABILITY, d.getOpposite(), level, worldPosition.relative(d)).ifPresent(n -> {
          nodes.add(n);
          //Maximum depth of 2
          nodes.addAll(n.getChildNodes(d.getOpposite()));
        });
      }
    }
    setChanged();
    return nodes;
  }

  public Stream<ContainerBlockHolder> getNodePositions() {
    return nodes.stream().map(INetworkNode::getProxy).map(h ->
      h.setBlockState(ServerLifecycleHooks.getCurrentServer()
                          .getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, h.getWorldID()))
                          .getBlockState(h.getPos())
      ));
  }

  public void writeExtended(PacketBuffer buf) {
    updateAdjacentNetwork();
    List<ContainerBlockHolder> list = getNodePositions().collect(Collectors.toList());
    buf.writeVarInt(list.size());

    for (ContainerBlockHolder h : list) {
      ContainerBlockHolder.writeTo(h, buf);
    }
  }
}
