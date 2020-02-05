package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlockShape;
import alpvax.advancedautocrafting.block.axial.AxialPart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static alpvax.advancedautocrafting.block.WireBlock.ConnectionState;
import static alpvax.advancedautocrafting.block.WireBlock.Shape;

public class WireBlock2 extends AxialBlock<ConnectionState> {
  public static final AxialBlockShape<ConnectionState> WIRE_SHAPE = AxialBlockShape.<ConnectionState>builder()
      .withCore(Shape.CORE_RADIUS / 16)//, new ResourceLocation(AdvancedAutocrafting.MODID, "block/wire"))
      .withPart(new AxialPart<>(
          "wire",
          Shape.WIRE_RADIUS / 16F,
          0F,
          0.5F,
          ConnectionState.CONNECTION, ConnectionState.INTERFACE
      )
          .face(Direction.SOUTH, null))
      .withPart(new AxialPart<>(
          "interface",
          Shape.INTERFACE_RADIUS / 16F,
          0F,
          Shape.INTERFACE_WIDTH / 16F,
          ConnectionState.INTERFACE
      )
          .face(Direction.SOUTH, f -> f.uvs(0, 0, 16, 16), true))
      .withPart(new AxialPart<>(
          "disabled",
          Shape.DISABLED_RADIUS / 16F,
          0.5F - Shape.DISABLED_WIDTH / 16F - Shape.CORE_RADIUS / 16,
          0.5F - Shape.CORE_RADIUS / 16,
          ConnectionState.DISABLED
      )
          .face(Direction.NORTH, f -> f.uvs(0, 0, 16, 16), true)
          .face(Direction.SOUTH, null)
      );

  public WireBlock2(Block.Properties properties) {
    super(properties,
        WIRE_SHAPE,
        d -> EnumProperty.create(d.getName(), WireBlock.ConnectionState.class)
    );
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.makeConnections(context.getWorld(), context.getPos());
  }

  private BlockState withConnectionState(BlockState bState, Direction dir, ConnectionState cState) {
    return bState.with(getConnectionProp(dir), cState);
  }

  public BlockState makeConnections(IBlockReader world, BlockPos thisPos) {
    BlockState state = getDefaultState();
    for(Direction d : ALL_DIRECTIONS) {
      BlockPos pos = thisPos.offset(d);
      state = withConnectionState(state, d, makeConnection(state, world, thisPos, d, pos));
    }
    return state;
  }

  public ConnectionState makeConnection(BlockState state, IBlockReader world, BlockPos thisPos, Direction dir, BlockPos neighborPos) {
    if(state.get(getConnectionProp(dir)) == ConnectionState.DISABLED) {
      return ConnectionState.DISABLED;
    }
    BlockState neighbor = world.getBlockState(neighborPos);
    IProperty<ConnectionState> prop = getConnectionProp(dir.getOpposite());
    if(neighbor.has(prop)) {
      if(neighbor.get(prop) == ConnectionState.DISABLED) {
        return ConnectionState.NONE;
      }
      return ConnectionState.CONNECTION;
    }
    /*if(WIRE_BLOCKS.contains(neighbor.getBlock())) { //TODO: Convert to block tag
      return ConnectionState.CONNECTION;
    }*/
    TileEntity tile = world.getTileEntity(neighborPos);
    if(tile != null && tile.getCapability(Capabilities.NODE_CAPABILITY).isPresent()) {
      return ConnectionState.INTERFACE;
    }
    return ConnectionState.NONE;
  }

  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    BlockPos dPos = fromPos.subtract(pos);
    Direction d = Direction.byLong(dPos.getX(), dPos.getY(), dPos.getZ());
    worldIn.setBlockState(pos, withConnectionState(state, d, makeConnection(state, worldIn, pos, d, fromPos)), 2);
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }
}
