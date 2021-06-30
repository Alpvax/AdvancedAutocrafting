package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteMasterBlock extends Block {
  public RemoteMasterBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new RemoteMasterTileEntity();
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onRemove(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && !isMoving) {
      TileEntity tileentity = worldIn.getBlockEntity(pos);
      if (tileentity instanceof RemoteMasterTileEntity) {
        ((RemoteMasterTileEntity)tileentity).dropItems(worldIn, pos, newState);
        worldIn.updateNeighbourForOutputSignal(pos, this);
      }

      //noinspection ConstantConditions
      super.onRemove(state, worldIn, pos, newState, isMoving);
    }
  }

  @Nonnull
  @SuppressWarnings("deprecation")
  @Override
  public ActionResultType use(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
    if (!worldIn.isClientSide) {
      final TileEntity tileEntity = worldIn.getBlockEntity(pos);
      if (tileEntity instanceof RemoteMasterTileEntity && this.interactWith(worldIn, pos, player, hand))
        NetworkHooks.openGui((ServerPlayerEntity) player, (RemoteMasterTileEntity) tileEntity, pos);
    }
    return ActionResultType.SUCCESS;
  }

  //TODO: Make this do something useful?
  private boolean interactWith(World worldIn, BlockPos pos, PlayerEntity player, Hand hand) {
    RemoteMasterTileEntity tile = AABlocks.TileTypes.REMOTE_MASTER.get().getBlockEntity(worldIn, pos);
    if (tile == null) {
      return false;
    }
    ItemStack stack = player.getItemInHand(hand);
    if(!stack.isEmpty()) {
      if(stack.getItem() instanceof BlockItem) {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        tile.getRemotePositions().forEach((p) -> worldIn.setBlock(p, state, Constants.BlockFlags.DEFAULT));
        return false;
      } else if(tile.inventory.isItemValid(0, stack)) {
        tile.addItem(stack.copy());
        stack.setCount(0);
        return false;
      }
      return true;
    } else {
      tile.getRemotePositions().forEach((p) -> worldIn.removeBlock(p, false));
      return false;
    }
  }
}
