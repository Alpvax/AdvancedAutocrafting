package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class WireBlock extends Block {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  public static enum ConnectionState implements IStringSerializable {
    NONE,
    CONNECTION,
    INTERFACE,
    DISABLED;

    private static final ConnectionState[] VALUES = values();
    private final String name;

    private ConnectionState() {
      name = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return getName();
    }
  }

  public static final Map<Direction, EnumProperty<ConnectionState>> DIR_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
    for(Direction d : ALL_DIRECTIONS) {
      map.put(d, EnumProperty.create(d.getName(), ConnectionState.class));
    }
  });

  public static class Shape {
    public static final float CORE_RADIUS = 3;
    public static final float WIRE_RADIUS = 2;
    public static final float INTERFACE_RADIUS = 6;
    public static final float INTERFACE_WIDTH = 1;
  }

  protected VoxelShape coreShape;
  protected Map<Direction, VoxelShape> wireShapes;
  protected Map<Direction, VoxelShape> interfaceShapes;

  public WireBlock(Properties properties) {
    super(properties);
    BlockState state = stateContainer.getBaseState();
    for(Direction d : ALL_DIRECTIONS) {
      state = withConnectionState(state, d, ConnectionState.NONE);
    }
    setDefaultState(state);
    setupShapes(Shape.CORE_RADIUS, Shape.WIRE_RADIUS, Shape.INTERFACE_RADIUS, Shape.INTERFACE_WIDTH);
  }

  private BlockState withConnectionState(BlockState bState, Direction dir, ConnectionState cState) {
    return bState.with(DIR_TO_PROPERTY_MAP.get(dir), cState);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    for(EnumProperty<ConnectionState> prop : DIR_TO_PROPERTY_MAP.values()) {
      builder.add(prop);
    }
  }

  @Override
  public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return false;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.makeConnections(context.getWorld(), context.getPos());
  }

  protected void setupShapes(float coreRadius, float wireRadius, float interfaceRadius, float interfaceWidth) {
    setupShapes(((double) coreRadius) / 16D, ((double) wireRadius) / 16D, ((double) interfaceRadius) / 16D, ((double) interfaceWidth) / 16D);
  }
  protected void setupShapes(double coreRadius, double wireRadius, double interfaceRadius, double interfaceWidth) {
    double coreMin = 0.5 - coreRadius;
    double coreMax = 0.5 + coreRadius;
    coreShape = VoxelShapes.create(coreMin, coreMin, coreMin, coreMax, coreMax, coreMax);
    wireShapes = new HashMap<>();
    interfaceShapes = new HashMap<>();

    for(Direction d : ALL_DIRECTIONS) {
      Vec3i vec = d.getDirectionVec();
      int x = vec.getX();
      int y = vec.getY();
      int z = vec.getZ();
      double wminx, wmaxx, wminy, wmaxy, wminz, wmaxz; // Wire
      double iminx, imaxx, iminy, imaxy, iminz, imaxz; // Interface
      if(x == 0) {
        wminx = 0.5 - wireRadius;
        wmaxx = 0.5 + wireRadius;
        iminx = 0.5 - interfaceRadius;
        imaxx = 0.5 + interfaceRadius;
      } else if(x < 0) {
        wminx = 0;
        wmaxx = coreMin;
        iminx = 0;
        imaxx = interfaceWidth;
      } else {
        wminx = coreMax;
        wmaxx = 1;
        iminx = 1 - interfaceWidth;
        imaxx = 1;
      }
      if(y == 0) {
        wminy = 0.5 - wireRadius;
        wmaxy = 0.5 + wireRadius;
        iminy = 0.5 - interfaceRadius;
        imaxy = 0.5 + interfaceRadius;
      } else if(y < 0) {
        wminy = 0;
        wmaxy = coreMin;
        iminy = 0;
        imaxy = interfaceWidth;
      } else {
        wminy = coreMax;
        wmaxy = 1;
        iminy = 1 - interfaceWidth;
        imaxy = 1;
      }
      if(z == 0) {
        wminz = 0.5 - wireRadius;
        wmaxz = 0.5 + wireRadius;
        iminz = 0.5 - interfaceRadius;
        imaxz = 0.5 + interfaceRadius;
      } else if(z < 0) {
        wminz = 0;
        wmaxz = coreMin;
        iminz = 0;
        imaxz = interfaceWidth;
      } else {
        wminz = coreMax;
        wmaxz = 1;
        iminz = 1 - interfaceWidth;
        imaxz = 1;
      }
      wireShapes.put(d, VoxelShapes.create(wminx, wminy, wminz, wmaxx, wmaxy, wmaxz));
      interfaceShapes.put(d, VoxelShapes.create(iminx, iminy, iminz, imaxx, imaxy, imaxz));
    }
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    List<VoxelShape> shapes = new ArrayList<>();
    for(Direction d : ALL_DIRECTIONS) {
      EnumProperty<ConnectionState> prop = DIR_TO_PROPERTY_MAP.get(d);
      switch(state.get(prop)) {
        case INTERFACE:
          shapes.add(interfaceShapes.get(d));
          // Do not break, need to add wire too
        case CONNECTION:
          shapes.add(wireShapes.get(d));
          break;
      }
    }
    return VoxelShapes.or(coreShape, shapes.toArray(new VoxelShape[0]));
  }

  public static EnumProperty<ConnectionState> getConnectionProp(Direction d) {
    return DIR_TO_PROPERTY_MAP.get(d);
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
    EnumProperty<ConnectionState> prop = getConnectionProp(dir.getOpposite());
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
