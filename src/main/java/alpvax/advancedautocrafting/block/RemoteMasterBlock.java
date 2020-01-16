package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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

  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && !isMoving) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof RemoteMasterTileEntity) {
        ((RemoteMasterTileEntity)tileentity).dropItems(worldIn, pos, newState);
        worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (p_225533_2_.isRemote) {
      return ActionResultType.SUCCESS;
    } else {
      this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_);
      return ActionResultType.SUCCESS;
    }
  }

  //TODO: Make this do something useful
  private void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
    TileEntity t = worldIn.getTileEntity(pos);
    if(t instanceof RemoteMasterTileEntity) {
      RemoteMasterTileEntity tile = (RemoteMasterTileEntity)t;
      ItemStack stack = player.getHeldItemMainhand();
      Hand hand = Hand.MAIN_HAND;
      if(stack.isEmpty()) {
        stack = player.getHeldItemOffhand();
        hand = Hand.OFF_HAND;
      }
      if(!stack.isEmpty()) {
        if(stack.getItem() instanceof BlockItem) {
          BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
          tile.getRemotePositions().stream().forEach((p) -> worldIn.setBlockState(p, state));
        } else if(AAUtil.hasPosition(stack)) {
          tile.addItem(stack.copy());
          stack.setCount(0);
          return;
        }
      } else {
        tile.getRemotePositions().stream().forEach((p) -> worldIn.setBlockState(p, Blocks.AIR.getDefaultState()));
      }
    }
  }
}
