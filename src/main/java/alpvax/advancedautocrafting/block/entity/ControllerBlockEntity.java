package alpvax.advancedautocrafting.block.entity;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.init.AABlocks;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerBlockEntity extends BlockEntity implements MenuProvider {
    private final INetworkNode network = new SimpleNetworkNode(this.worldPosition);
    private final LazyOptional<INetworkNode> networkCapability = LazyOptional.of(() -> network);

    public ControllerBlockEntity(BlockPos pos, BlockState state) {
        super(AABlocks.Entities.CONTROLLER.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return AABlocks.CONTROLLER.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ControllerContainer(id, playerInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == AAReference.NODE_CAPABILITY ? networkCapability.cast() : super.getCapability(cap, side);
    }
}
