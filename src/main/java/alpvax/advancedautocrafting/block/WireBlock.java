package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.craftnetwork.Capabilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashSet;
import java.util.Set;

public class WireBlock extends SixWayBlock {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  public static final Set<Block> WIRE_BLOCKS = new HashSet();

  public WireBlock(Properties properties) {
    super(0.25F, properties);
    setDefaultState(stateContainer.getBaseState()
        .with(NORTH, Boolean.valueOf(false))
        .with(EAST, Boolean.valueOf(false))
        .with(SOUTH, Boolean.valueOf(false))
        .with(WEST, Boolean.valueOf(false))
        .with(UP, Boolean.valueOf(false))
        .with(DOWN, Boolean.valueOf(false))
    );
    WIRE_BLOCKS.add(this);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
  }

  @Override
  public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.makeConnections(context.getWorld(), context.getPos());
  }

  public BlockState makeConnections(IBlockReader world, BlockPos thisPos) {
    BlockState state = getDefaultState();
    for(Direction d : ALL_DIRECTIONS) {
      BlockPos pos = thisPos.offset(d);
      TileEntity tile = world.getTileEntity(pos);
      Block block = world.getBlockState(pos).getBlock();
      if(WIRE_BLOCKS.contains(block) || (tile != null && tile.getCapability(Capabilities.NODE_CAPABILITY).isPresent())) {
        state.with(FACING_TO_PROPERTY_MAP.get(d), true);
      }
    }
    return state;
  }
}
