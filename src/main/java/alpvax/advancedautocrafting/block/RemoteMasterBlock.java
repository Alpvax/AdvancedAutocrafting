package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
  public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && !isMoving) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof RemoteMasterTileEntity) {
        ((RemoteMasterTileEntity)tileentity).dropItems(worldIn, pos, newState);
        worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  @Nonnull
  @SuppressWarnings("deprecation")
  @Override
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    if (worldIn.isRemote) {
      return ActionResultType.SUCCESS;
    } else {
      final TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof RemoteMasterTileEntity && this.interactWith(worldIn, pos, player, hand))
        NetworkHooks.openGui((ServerPlayerEntity)player, (RemoteMasterTileEntity)tileEntity, pos);
      return ActionResultType.SUCCESS;
    }
  }

  //TODO: Make this do something useful?
  private boolean interactWith(World worldIn, BlockPos pos, PlayerEntity player, Hand hand) {
    RemoteMasterTileEntity tile = (RemoteMasterTileEntity)worldIn.getTileEntity(pos);
    ItemStack stack = player.getHeldItem(hand);
    if(!stack.isEmpty()) {
      if(stack.getItem() instanceof BlockItem) {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        tile.getRemotePositions().forEach((p) -> worldIn.setBlockState(p, state));
        return false;
      } else if(AAUtil.hasPosition(stack)) {
        tile.addItem(stack.copy());
        stack.setCount(0);
        return false;
      }
      return true;
    } else {
      tile.getRemotePositions().forEach((p) -> worldIn.setBlockState(p, Blocks.AIR.getDefaultState()));
      return false;
    }
  }
}
