package alpvax.advancedautocrafting.block.wire;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class MultiPartWireBlock extends Block implements SimpleWaterloggedBlock {

    public MultiPartWireBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }
}
