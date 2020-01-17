package alpvax.advancedautocrafting.container;


import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.items.SlotItemHandler;

public class RemoteMasterContainer extends Container {
  private final RemoteMasterTileEntity tileentity;

  /**
   * Logical-client-side constructor, called from {@link ContainerType#create(IContainerFactory)}
   * Calls the logical-server-side constructor with the TileEntity at the pos in the PacketBuffer
   */
  public RemoteMasterContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
    this(windowId, playerInventory, (RemoteMasterTileEntity)playerInventory.player.world.getTileEntity(data.readBlockPos()));
  }

  public RemoteMasterContainer(final int id, final PlayerInventory playerInventory, final RemoteMasterTileEntity tile) {
    super(AAContainerTypes.REMOTE_MASTER.get(), id);
    tileentity = tile;

    for(int j = 0; j < 3; ++j) {
      for(int k = 0; k < 9; ++k) {
        this.addSlot(new SlotItemHandler(tile.inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
      }
    }

    //Player inventory
    for(int l = 0; l < 3; ++l) {
      for(int j1 = 0; j1 < 9; ++j1) {
        this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 85 + l * 18));
      }
    }
    //Hotbar
    for(int i1 = 0; i1 < 9; ++i1) {
      this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 143));
    }
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return tileentity.canPlayerUse(playerIn);
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index < 27) {
        if (!this.mergeItemStack(itemstack1, 27, this.inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }

    return itemstack;
  }
}
