package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllerBlock extends Block {
  public ControllerBlock(Block.Properties properties) {
    super(properties);
  }
  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new ControllerTileEntity();
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
  public ActionResultType use(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
    final TileEntity tileEntity = worldIn.getBlockEntity(pos);
    if (tileEntity instanceof ControllerTileEntity) {
      if (!worldIn.isClientSide) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (ControllerTileEntity) tileEntity, pos);
      }
      return ActionResultType.SUCCESS;
    }
    return super.use(state, worldIn, pos, player, hand, rayTraceResult);
  }
}
