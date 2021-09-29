package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteMasterBlock extends Block implements EntityBlock {
  public RemoteMasterBlock(Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new RemoteMasterTileEntity(pos, state);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && !isMoving) {
      level.getBlockEntity(pos, AABlocks.TileTypes.REMOTE_MASTER.get()).ifPresent(tile -> {
        tile.dropItems(level, pos, newState);
        level.updateNeighbourForOutputSignal(pos, this);
      });

      //noinspection ConstantConditions
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }

  @Nonnull
  @SuppressWarnings("deprecation")
  @Override
  public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rayTraceResult) {
    if (!level.isClientSide) {
      level.getBlockEntity(pos, AABlocks.TileTypes.REMOTE_MASTER.get()).ifPresent(tile -> {
        if (this.interactWith(level, pos, player, hand))
          NetworkHooks.openGui((ServerPlayer) player, tile, pos);
      });
    }
    return InteractionResult.SUCCESS;
  }

  //TODO: Make this do something useful?
  private boolean interactWith(Level level, BlockPos pos, Player player, InteractionHand hand) {
    RemoteMasterTileEntity tile = AABlocks.TileTypes.REMOTE_MASTER.get().getBlockEntity(level, pos);
    if (tile == null) {
      return false;
    }
    ItemStack stack = player.getItemInHand(hand);
    if(!stack.isEmpty()) {
      if(stack.getItem() instanceof BlockItem) {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        tile.getRemotePositions().forEach((p) -> level.setBlock(p, state, Constants.BlockFlags.DEFAULT));
        return false;
      } else if(tile.inventory.isItemValid(0, stack)) {
        tile.addItem(stack.copy());
        stack.setCount(0);
        return false;
      }
      return true;
    } else {
      tile.getRemotePositions().forEach((p) -> level.removeBlock(p, false));
      return false;
    }
  }
}
