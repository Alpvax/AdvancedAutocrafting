package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class RemoteMasterTileEntity extends TileEntity {
  private LazyOptional<IItemHandler> items = LazyOptional.of(this::makeInventory);
  private LazyOptional<INetworkNode> network = LazyOptional.of(this::makeNetworkNode);

  public RemoteMasterTileEntity() {
    super(AABlocks.TileTypes.REMOTE_MASTER.get());
  }

  private IItemHandler makeInventory() {
    return new ItemStackHandler(27) {
      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return AAUtil.hasPosition(stack);
      }

      @Override
      public int getSlotLimit(int slot) {
        return 1;
      }

      @Override
      protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        //TODO:network.markNodeDirty();
      }
    };
  }

  private INetworkNode makeNetworkNode() {
    return new INetworkNode() {
      @Override
      public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
        return RemoteMasterTileEntity.this.getItems().stream().map((stack) ->
            new SimpleNetworkNode(AAUtil.readPosFromItemStack(stack))).collect(Collectors.toCollection(NonNullList::create)
        );
      }

      @Override
      public BlockPos getPos() {
        return RemoteMasterTileEntity.this.pos;
      }
    };
  }

  private NonNullList<ItemStack> getItems() {
    NonNullList<ItemStack> list = NonNullList.create();
    RemoteMasterTileEntity.this.items.ifPresent((items) -> {
      for(int i = 0; i < items.getSlots(); i++) {
        ItemStack stack = items.getStackInSlot(i);
        if(!stack.isEmpty()) {
          list.add(stack);
        }
      }
    });
    return list;
  }

  public NonNullList<BlockPos> getRemotePositions() {
    return getItems().stream().map((stack) -> AAUtil.readPosFromItemStack(stack)).filter(Predicates.notNull()).collect(Collectors.toCollection(NonNullList::create));
  }

  public void dropItems(World worldIn, BlockPos pos, BlockState newState) {
    this.items.ifPresent((items) -> {
      InventoryHelper.dropItems(worldIn, pos, getItems());
    });
  }

  public void addItem(ItemStack stack) {
    items.ifPresent(items -> {
      for(int i = 0; i < items.getSlots(); i++) {
        ItemStack s = items.getStackInSlot(i);
        if(s.isEmpty()) {
          items.insertItem(i, stack, false);
          break;
        }
      }
    });
  }
}
