package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class ControllerTileEntity extends TileEntity  implements INamedContainerProvider {
  public ControllerTileEntity() {
    super(AABlocks.TileTypes.CONTROLLER.get());
  }

  @Override
  public ITextComponent getDisplayName() {
    return AABlocks.CONTROLLER.get().getNameTextComponent();
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
    return null;//TODO:new ControllerContainer(id, playerInventory, this);
  }
}
