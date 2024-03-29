package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.entity.ControllerBlockEntity;
import alpvax.advancedautocrafting.init.AAContainerTypes;
import net.minecraft.world.entity.player.Inventory;

public class ControllerContainer extends AbstractBlockEntityContainer<ControllerBlockEntity> {
    public ControllerContainer(int id, Inventory playerInventory, ControllerBlockEntity tile) {
        super(AAContainerTypes.CONTROLLER.get(), id, tile);
    }
}
