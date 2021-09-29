package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class RemotePositionMarkerBlock extends Block {
  public RemotePositionMarkerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    if (player.isCrouching() && level instanceof Level l) {
      ItemStack stack = new ItemStack(AAItems.REMOTE_POS.get());
      BlockPosLootFunction.write(stack.getOrCreateTag(), l, pos);
    }
    return super.getPickBlock(state, target, level, pos, player);
  }
}
