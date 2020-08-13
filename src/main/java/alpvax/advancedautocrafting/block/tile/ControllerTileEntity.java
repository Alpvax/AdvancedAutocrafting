package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.api.util.UniversalPos;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ControllerTileEntity extends TileEntity  implements INamedContainerProvider {
  private final INetworkNode network = new SimpleNetworkNode(this.pos);
  private final LazyOptional<INetworkNode> networkCapability = LazyOptional.of(() -> network);
  public ControllerTileEntity() {
    super(AABlocks.TileTypes.CONTROLLER.get());
  }

  @Override
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
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
    return AABlocks.CONTROLLER.get().func_235333_g_();//.getNameTextComponent();
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
    return new ControllerContainer(id, playerInventory, this);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == Capabilities.NODE_CAPABILITY ? networkCapability.cast() : super.getCapability(cap, side);
  }


  //TODO: replace with more full fledged network handling
  private final List<INetworkNode> nodes = new ArrayList<>();
  private final List<ProxyBlockSource> nodePositions = new ArrayList<>();
  public  List<INetworkNode> updateAdjacentNetwork() {
    nodes.clear();
    nodePositions.clear();
    if (world != null) {
      nodes.add(network);
      for (Direction d : Direction.values()) {
        UniversalPos p = new UniversalPos(world, pos.offset(d));
        p.getCapability(Capabilities.NODE_CAPABILITY, d.getOpposite()).ifPresent(n -> {
          nodes.add(n);
          //Maximum depth of 2
          nodes.addAll(n.getChildNodes(d.getOpposite()));
        });
      }
    }
    nodes.stream()
        .map(n -> new ProxyBlockSource(world, n.getPos()))
        .forEachOrdered(nodePositions::add);
    markDirty();
    return nodes;
  }

  public List<ProxyBlockSource> getNodePositions() {
    return nodePositions;
  }
}
