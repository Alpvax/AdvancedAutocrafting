package alpvax.advancedautocrafting.container;


import alpvax.advancedautocrafting.block.entity.RemoteMasterBlockEntity;
import alpvax.advancedautocrafting.init.AAContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class RemoteMasterContainer extends AbstractBlockEntityContainer<RemoteMasterBlockEntity> {
    public RemoteMasterContainer(final int id, final Inventory playerInventory, final RemoteMasterBlockEntity tile) {
        super(AAContainerTypes.REMOTE_MASTER.get(), id, tile);

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                addSlot(new SlotItemHandler(tile.inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        //Player inventory
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 85 + l * 18));
            }
        }
        //Hotbar
        for (int i1 = 0; i1 < 9; ++i1) {
            addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 143));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!moveItemStackTo(itemstack1, 27, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
