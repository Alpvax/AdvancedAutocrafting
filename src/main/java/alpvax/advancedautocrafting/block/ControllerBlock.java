package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.entity.ControllerBlockEntity;
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
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ControllerBlock extends Block implements EntityBlock {
  public ControllerBlock(Block.Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ControllerBlockEntity(pos, state);
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
  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
    return level.getBlockEntity(pos, AABlocks.Entities.CONTROLLER.get()).map(tile -> {
      if (!level.isClientSide) {
        NetworkHooks.openGui((ServerPlayer) player, tile, pos);
      }
      return InteractionResult.SUCCESS;
    })
    .orElseGet(() -> super.use(state, level, pos, player, hand, rayTraceResult));
  }
}
