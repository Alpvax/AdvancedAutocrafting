package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.entity.RemoteMasterBlockEntity;
import alpvax.advancedautocrafting.init.AABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class RemoteMasterBlock extends Block implements EntityBlock {
    public RemoteMasterBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RemoteMasterBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !isMoving) {
            level.getBlockEntity(pos, AABlocks.Entities.REMOTE_MASTER.get()).ifPresent(tile -> {
                tile.dropItems(level, pos, newState);
                level.updateNeighbourForOutputSignal(pos, this);
            });
            //TODO: move out of moving check?
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (!level.isClientSide) {
            level.getBlockEntity(pos, AABlocks.Entities.REMOTE_MASTER.get()).ifPresent(tile -> {
                if (this.interactWith(level, pos, player, hand, rayTraceResult))
                    NetworkHooks.openGui((ServerPlayer) player, tile, pos);
            });
        }
        return InteractionResult.SUCCESS;
    }

    //TODO: Make this do something useful?
    private boolean interactWith(Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        RemoteMasterBlockEntity tile = AABlocks.Entities.REMOTE_MASTER.get().getBlockEntity(level, pos);
        if (tile == null) {
            return false;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            if (tile.inventory.isItemValid(0, stack)) {
                tile.addItem(stack.copy());
                stack.setCount(0);
                return false;
            } else if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                tile.getRemotePositions().forEach((p) -> {
                    BlockState state = block.getStateForPlacement(new BlockPlaceContext(p.getLevel(), player, hand, stack, hitResult));
                    if (state == null) {
                        state = block.defaultBlockState();
                    }
                    p.getLevel().setBlock(p.getPosition(), state, Block.UPDATE_ALL);

                });
                return false;
            }
            return true;
        } else {
            tile.getRemotePositions().forEach((p) -> level.removeBlock(p.getPosition(), false));
            return false;
        }
    }
}
