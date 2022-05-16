package alpvax.advancedautocrafting.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * A "slotless" IItemHandler which contains an unlimited number of ItemStacks, and can always contain one more stack
 * Items will freely move around to leave no empty slots (except for a single always-empty slot at the end).
 *
 * <em>DO NOT TRUST THE SLOT INDEX TO REMAIN THE SAME AT ANY POINT</em>
 */
public class ItemListHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<ListTag> {
    private NonNullList<ItemStack> stacks;

    public ItemListHandler() {
        stacks = NonNullList.of(ItemStack.EMPTY);
    }
    public ItemListHandler(NonNullList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    @Override
    public ListTag serializeNBT() {
        ListTag list = new ListTag();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                list.add(itemTag);
            }
        }
        return list;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        NonNullList<ItemStack> list = NonNullList.withSize(nbt.size() + 1, ItemStack.EMPTY);
        nbt.stream().map(tag -> ItemStack.of((CompoundTag) tag)).forEach(list::add);
        stacks.clear();
        stacks = list;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot == -1) {
            slot = stacks.size();
        }
        if (isItemValid(slot, stack) && setStack(slot, stack)) {
            onContentsChanged(slot);
        }
    }

    protected final boolean setStack(int slot, @Nonnull ItemStack stack) {
        if (validateSlot(slot)) {
            if (!stack.isEmpty()) {
                stacks.add(stack);
                return true;
            }
        } else {
            if (stack.isEmpty()) {
                stacks.remove(slot);
            } else {
                stacks.set(slot, stack);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getSlots() {
        return stacks.size() + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (validateSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!isItemValid(slot, stack)) {
            return stack;
        }

        validateSlot(slot);
        int remaining = stack.getCount();
        ItemStack existing = getStackInSlot(slot);
        int space = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }
            space -= existing.getCount();
        }
        if (space <= 0) {
            return stack;
        }
        int stored = Math.min(space, stack.getCount());
        remaining -= stored;

        ItemStack rest = remaining > 0 ? ItemHandlerHelper.copyStackWithSize(stack, remaining) : ItemStack.EMPTY;
        if (!simulate) {
            if (existing.isEmpty()) {
                setStackInSlot(slot, remaining > 0 ? ItemHandlerHelper.copyStackWithSize(stack, stored) : stack);
            } else {
                existing.grow(stored);
            }
            onContentsChanged(slot);
        }

        return rest;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        validateSlot(slot);

        ItemStack existing = getStackInSlot(slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (simulate) {
                return existing.copy();
            } else {
                if (setStack(slot, ItemStack.EMPTY)) {
                    onContentsChanged(slot);
                }
                return existing;
            }
        } else if (!simulate && setStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract))) {
            onContentsChanged(slot);
        }

        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    /**
     * Check whether the numbered slot is within the range
     * @param slot the slot to check
     * @return true if the slot is the last "always empty" slot
     */
    protected boolean validateSlot(int slot)
    {
        int size = stacks.size();
        if (slot < 0 || slot > size) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + (size + 1) + ")");
        }
        return slot == size;
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    protected void onContentsChanged(int slot)
    {

    }
}
