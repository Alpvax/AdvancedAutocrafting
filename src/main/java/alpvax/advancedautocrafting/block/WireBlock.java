package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlockShape;
import alpvax.advancedautocrafting.block.axial.AxialPart;
import alpvax.advancedautocrafting.block.axial.IAxialPartInstance;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkConnectionNode;
import alpvax.advancedautocrafting.craftnetwork.connection.ISimpleCraftNetworkNodeFactory;
import alpvax.advancedautocrafting.craftnetwork.manager.NodeManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class WireBlock extends AxialBlock<WireBlock.ConnectionState> implements IWaterLoggable, ISimpleCraftNetworkNodeFactory {
  @Override
  public INetworkNode createNode(BlockState state, IWorldReader world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == this ? new SimpleNetworkConnectionNode(world, pos) {
      @Nonnull
      @Override
      public Connectivity getConnectivity(Direction dir) {
        IProperty<ConnectionState> prop = getConnectionProp(dir);
        if (prop != null) {
          switch (getBlockState().get(prop)) {
            case DISABLED:
              return Connectivity.BLOCK;
            case INTERFACE:
            case CONNECTION:
              return Connectivity.CONNECT;
            case NONE:
              return Connectivity.ACCEPT;
          }
        }
        return Connectivity.BLOCK;
      }
    } : null;
  }

  public enum ConnectionState implements IStringSerializable {
    NONE,
    CONNECTION,
    INTERFACE,
    DISABLED;

    private static final ConnectionState[] VALUES = values();
    private final String name;

    ConnectionState() {
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

  private static final float CORE_RADIUS = 3/16F;
  public static final AxialBlockShape<ConnectionState> WIRE_SHAPE = AxialBlockShape.builder("base_wire", ConnectionState.class)
      .withCore(CORE_RADIUS)
      .withPart(new AxialPart<>(
          "arm",
          2/16F,
          0F,
          0.5F - CORE_RADIUS,
          ConnectionState.CONNECTION, ConnectionState.INTERFACE
      )
          .face(Direction.SOUTH, null))
      .withPart(new AxialPart<>(
          "interface",
          6/16F,
          0F,
          1/16F,
          ConnectionState.INTERFACE
      )
          .face(Direction.SOUTH, f -> f.uvs(0, 0, 16, 16), true))
      .withPart(new AxialPart<>(
          "disabled",
          2.5F / 16F,
          0.5F - 1/16F - CORE_RADIUS,
          0.5F - CORE_RADIUS,
          ConnectionState.DISABLED
      )
          .face(Direction.NORTH, f -> f.uvs(0, 0, 16, 16), true)
          .face(Direction.SOUTH, null)
      );

  public WireBlock(Block.Properties properties) {
    super(properties, WIRE_SHAPE);
    setDefaultState(getDefaultState().with(WATERLOGGED, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(WATERLOGGED);
  }

  @Nullable
  @Override
  protected IProperty<ConnectionState> buildPropertyForDirection(Direction d) {
    return EnumProperty.create(d.getName(), ConnectionState.class);
  }

  @Nonnull
  @Override
  protected ConnectionState getDefaultPropertyValue(Direction d) {
    return ConnectionState.NONE;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.makeConnections(context.getWorld(), context.getPos());
  }

  private BlockState withConnectionState(BlockState bState, Direction dir, ConnectionState cState) {
    IProperty<ConnectionState> prop = getConnectionProp(dir);
    return prop == null ? bState : bState.with(prop, cState);
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

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    BlockPos dPos = fromPos.subtract(pos);
    Direction d = Direction.byLong(dPos.getX(), dPos.getY(), dPos.getZ());
    worldIn.setBlockState(pos, withConnectionState(state, d, makeConnection(state, worldIn, pos, d, fromPos)), 2);
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    INetworkNode.handleNeighborChange(worldIn, pos, fromPos);
  }

  private BlockState getToggledState(BlockState state, IWorldReader world, BlockPos pos, Direction d) {
    IProperty<ConnectionState> prop = getConnectionProp(d);
    ConnectionState val = state.get(prop);
    if(val == ConnectionState.DISABLED) {
      state = withConnectionState(state, d, ConnectionState.NONE);
      return withConnectionState(state, d, makeConnection(state, world, pos, d, pos.offset(d)));
    } else {
      return withConnectionState(state, d, ConnectionState.DISABLED);
    }
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    ItemStack stack = player.getHeldItem(hand);
    if(!stack.isEmpty() && stack.getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent()) {
      // Multitool
      if (!worldIn.isRemote) {
        if (player.isShiftKeyDown()) {
          worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
          if (!player.isCreative()) {
            spawnAsEntity(worldIn, pos, new ItemStack(this));
          }
        } else {
          Vec3d start = new Vec3d(player.prevPosX, player.prevPosY + player.getEyeHeight(), player.prevPosZ);
          Vec3d end = start.add(player.getLook(0).scale(player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue()));
          Direction dir = rayTracePart(state, pos, start, end).direction();
          if (dir == null) {
            dir = rayTraceResult.getFace();
          }
          worldIn.setBlockState(pos, getToggledState(state, worldIn, pos, dir));
        }
      }
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, worldIn, pos, player, hand, rayTraceResult);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    NodeManager.get(worldIn, pos).addNode(createNode(state, worldIn, pos));
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    super.onReplaced(state, worldIn, pos, newState, isMoving);
    if (state.getBlock() != newState.getBlock()) {
      NodeManager.get(worldIn, pos).removeNode(pos);
    }
  }

  @Override
  protected VoxelShape getPartialBlockHighlight(BlockState state, IAxialPartInstance<ConnectionState> partInstance) {
    Direction d = partInstance.direction();
    if (d == null) {
      return getBlockShape().getCoreShape();
    }
    return getBlockShape().getAxialShape(d, state.get(getConnectionProp(d)));
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IFluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }
}
