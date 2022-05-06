package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.item.AAItems;
import alpvax.advancedautocrafting.util.IPositionReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class PositionMarkerBlock extends Block {
    public PositionMarkerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (player.isCrouching() && level instanceof Level l) {
            ItemStack stack = new ItemStack(AAItems.POSITION_MARKER.get());
            IPositionReference.PositionMarkerItemStack.setPosition(stack, l.dimension(), pos.immutable());
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }
}
