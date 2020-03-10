package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.craftnetwork.ControllerNetworkNode;
import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
  private final CraftNetwork network;
  private final ControllerNetworkNode networkNode;
  private LazyOptional<INetworkNode> networkCapability;

  public ControllerTileEntity() {
    super(AABlocks.TileTypes.CONTROLLER.get());
    networkNode = new ControllerNetworkNode(this);
    network = new CraftNetwork(networkNode);
    networkCapability = LazyOptional.of(() -> networkNode);
  }

  @Override
  public void markDirty() {
    markNetworkDirty();
    super.markDirty();
  }

  public CraftNetwork getNetwork() {
    return network;
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
    return AABlocks.CONTROLLER.get().getNameTextComponent();
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

  @Override
  public void tick() {
    network.update();
  }

  @Override
  public void setPos(BlockPos p_174878_1_) {
    super.setPos(p_174878_1_);
    markNetworkDirty();
  }

  @Override
  public void setWorldAndPos(World p_226984_1_, BlockPos p_226984_2_) {
    super.setWorldAndPos(p_226984_1_, p_226984_2_);
    markNetworkDirty();
  }

  public void markNetworkDirty() {
    network.markDirty(networkNode);
  }
}
