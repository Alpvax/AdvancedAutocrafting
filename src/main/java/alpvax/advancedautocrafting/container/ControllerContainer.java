package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import net.minecraft.world.entity.player.Inventory;

public class ControllerContainer extends AbstractTileEntityContainer<ControllerTileEntity> {
  public ControllerContainer(int id, Inventory playerInventory, ControllerTileEntity tile) {
    super(AAContainerTypes.CONTROLLER.get(), id, tile);
  }
}
