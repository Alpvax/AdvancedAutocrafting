package alpvax.advancedautocrafting.block.entity;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import alpvax.advancedautocrafting.util.IPositionReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class RemoteMasterBlockEntity extends BlockEntity implements MenuProvider {
    public ItemStackHandler inventory = new ItemStackHandler(27) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(Capabilities.POSITION_MARKER_CAPABILITY).isPresent();
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
    private final LazyOptional<IItemHandler> items = LazyOptional.of(() -> inventory);
    private final LazyOptional<INetworkNode> networkCapability = LazyOptional.of(this::makeNetworkNode);

    public RemoteMasterBlockEntity(BlockPos pos, BlockState state) {
        super(AABlocks.Entities.REMOTE_MASTER.get(), pos, state);
    }

    private INetworkNode makeNetworkNode() {
        return new INetworkNode() {
            @Override
            public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
                return RemoteMasterBlockEntity.this.getRemotePositions().stream()
                    .map(p -> new SimpleNetworkNode(p.getPosition()))
                    .collect(Collectors.toCollection(NonNullList::create));
            }

            @Override
            public BlockPos getPos() {
                return RemoteMasterBlockEntity.this.worldPosition;
            }
        };
    }

    private NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    public NonNullList<IPositionReference> getRemotePositions() {
        return getItems().stream()
            .map(stack -> stack.getCapability(Capabilities.POSITION_MARKER_CAPABILITY))
            .filter(LazyOptional::isPresent)
            .map(lazy -> lazy.resolve().orElseThrow())
            .collect(Collectors.toCollection(NonNullList::create));
    }

    public void dropItems(Level level, BlockPos pos, BlockState newState) {
        Containers.dropContents(level, pos, getItems());
    }

    public ItemStack addItem(ItemStack stack) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack s = inventory.getStackInSlot(i);
            if (s.isEmpty()) {
                return inventory.insertItem(i, stack, false);
            }
        }
        return stack;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("remoteItems"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("remoteItems", inventory.serializeNBT());
    }

  /*@Override
  public CompoundNBT getUpdateTag() {
    return super.getUpdateTag();
  }

  @Override
  public void handleUpdateTag(CompoundNBT tag) {

  }*/

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(AABlocks.REMOTE_MASTER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RemoteMasterContainer(id, playerInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == Capabilities.NODE_CAPABILITY ? networkCapability.cast() : super.getCapability(cap, side);
    }
}
