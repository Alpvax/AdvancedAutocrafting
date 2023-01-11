package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.entity.ControllerBlockEntity;
import alpvax.advancedautocrafting.init.AAContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ControllerContainer extends AbstractBlockEntityContainer<ControllerBlockEntity> {
    public ControllerContainer(int id, Inventory playerInventory, ControllerBlockEntity tile) {
        super(AAContainerTypes.CONTROLLER.get(), id, tile);
    }
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
