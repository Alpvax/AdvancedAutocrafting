package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import net.minecraft.entity.player.PlayerInventory;

public class ControllerContainer extends AbstractTileEntityContainer<ControllerTileEntity> {
  public ControllerContainer(int id, PlayerInventory playerInventory, ControllerTileEntity tile) {
    super(AAContainerTypes.CONTROLLER.get(), id, tile);
  }
}
