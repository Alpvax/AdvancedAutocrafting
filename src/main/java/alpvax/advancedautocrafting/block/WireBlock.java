package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlockShape;
import alpvax.advancedautocrafting.block.axial.AxialPart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class WireBlock extends AxialBlock<WireBlock.ConnectionState> implements IWaterLoggable {
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

    public boolean isNotDisabled() {
      return this != DISABLED;
    }

    @Nonnull
    @Override
    public String getSerializedName() {
      return name;
    }

    @Override
    public String toString() {
      return name;
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
    registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
  }

  @Override
  protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(WATERLOGGED);
  }

  @Nullable
  @Override
  protected Property<ConnectionState> buildPropertyForDirection(Direction d) {
    return EnumProperty.create(d.getSerializedName(), ConnectionState.class);
  }

  @Nonnull
  @Override
  protected ConnectionState getDefaultPropertyValue(Direction d) {
    return ConnectionState.NONE;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.makeConnections(context.getLevel(), context.getClickedPos());
  }

  private BlockState withConnectionState(BlockState bState, Direction dir, ConnectionState cState) {
    return getConnectionProp(dir).map(prop -> bState.setValue(prop, cState)).orElse(bState);
  }

  public BlockState makeConnections(IBlockReader world, BlockPos thisPos) {
    BlockState state = defaultBlockState();
    for(Direction d : ALL_DIRECTIONS) {
      BlockPos pos = thisPos.relative(d);
      state = withConnectionState(state, d, makeConnection(world, thisPos, d, pos));
    }
    return state;
  }

  /**
   * Do not call if state is DISABLED
   */
  public ConnectionState makeConnection(IBlockReader world, BlockPos thisPos, Direction dir, BlockPos neighborPos) {
    TileEntity tile = world.getBlockEntity(neighborPos);
    if(tile != null && tile.getCapability(Capabilities.NODE_CAPABILITY).isPresent()) {
      return ConnectionState.INTERFACE;
    }
    BlockState neighbor = world.getBlockState(neighborPos);
    return getConnectionProp(dir.getOpposite())
               .filter(prop -> neighbor.hasProperty(prop) && neighbor.getValue(prop).isNotDisabled())
               .map(prop -> ConnectionState.CONNECTION).orElse(ConnectionState.NONE);
    /*if(WIRE_BLOCKS.contains(neighbor.getBlock())) { //TODO: Convert to block tag
      return ConnectionState.CONNECTION;
    }*/
  }

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    BlockPos dPos = fromPos.subtract(pos);
    Direction d = Direction.fromNormal(dPos.getX(), dPos.getY(), dPos.getZ());
    getConnection(state, d).filter(ConnectionState::isNotDisabled).ifPresent(val ->
        worldIn.setBlock(pos, withConnectionState(state, d, makeConnection(worldIn, pos, d, fromPos)), 2)
    );
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }

  private BlockState getToggledState(BlockState state, IWorldReader world, BlockPos pos, Direction d) {
    return getConnection(state, d).map(val -> withConnectionState(state, d, val.isNotDisabled()
                                                 ? ConnectionState.DISABLED
                                                 : makeConnection(world, pos, d, pos.relative(d)))
    ).orElse(state);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    ItemStack stack = player.getItemInHand(hand);
    if(!stack.isEmpty() && stack.getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent()) {
      // Multitool
      if (!worldIn.isClientSide) {
        if (player.isCrouching()) {
          worldIn.removeBlock(pos, false);
          if (!player.isCreative()) {
            popResource(worldIn, pos, new ItemStack(this));
          }
        } else {
          Vector3d start = new Vector3d(player.xOld, player.yOld + player.getEyeHeight(), player.zOld);
          Vector3d end = start.add(player.getViewVector(0).scale(player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
          Direction dir = rayTracePart(state, pos, start, end).direction();
          if (dir == null) {
            dir = rayTraceResult.getDirection();
          }
          worldIn.setBlock(pos, getToggledState(state, worldIn, pos, dir), Constants.BlockFlags.DEFAULT);
        }
      }
      return ActionResultType.SUCCESS;
    }
    return super.use(state, worldIn, pos, player, hand, rayTraceResult);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }
}
