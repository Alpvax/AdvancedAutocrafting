package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RemotePositionMarkerBlock extends Block {
  public RemotePositionMarkerBlock(AbstractBlock.Properties properties) {
    super(properties);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    if (player.isSneaking() && world instanceof World) {
      ItemStack stack = new ItemStack(AAItems.REMOTE_POS.get());
      BlockPosLootFunction.write(stack.getOrCreateTag(), (World) world, pos);
    }
    return super.getPickBlock(state, target, world, pos, player);
  }
}
