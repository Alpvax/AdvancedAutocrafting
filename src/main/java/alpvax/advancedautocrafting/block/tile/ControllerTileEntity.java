package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerTileEntity extends TileEntity  implements INamedContainerProvider {
  private INetworkNode network = new SimpleNetworkNode(this.pos);
  private LazyOptional<INetworkNode> networkCapability = LazyOptional.of(() -> network);
  public ControllerTileEntity() {
    super(AABlocks.TileTypes.CONTROLLER.get());
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
}
