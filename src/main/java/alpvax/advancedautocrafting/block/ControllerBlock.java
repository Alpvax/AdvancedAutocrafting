package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerBlock extends Block implements EntityBlock {
  public ControllerBlock(Block.Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
    return new ControllerTileEntity(pos, state);
  }

  /*public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && !isMoving) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof ControllerTileEntity) {
        ((ControllerTileEntity)tileentity).dropItems(worldIn, pos, newState);
        worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }*/

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rayTraceResult) {
    return level.getBlockEntity(pos, AABlocks.TileTypes.CONTROLLER.get()).map(tile -> {
      if (!level.isClientSide) {
        NetworkHooks.openGui((ServerPlayer) player, tile, pos);
      }
      return InteractionResult.SUCCESS;
    })
    .orElseGet(() -> super.use(state, level, pos, player, hand, rayTraceResult));
  }
}
