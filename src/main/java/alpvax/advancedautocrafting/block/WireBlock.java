package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlockShape;
import alpvax.advancedautocrafting.block.axial.AxialPart;
import alpvax.advancedautocrafting.init.AATags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Locale;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class WireBlock extends AxialBlock<WireBlock.ConnectionState> implements SimpleWaterloggedBlock {
    public enum ConnectionState implements StringRepresentable {
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

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final float CORE_RADIUS = 3 / 16F;
    public static final AxialBlockShape<ConnectionState> WIRE_SHAPE = AxialBlockShape.builder(
            "base_wire", ConnectionState.class)
        .withCore(CORE_RADIUS)
        .withPart(new AxialPart<>(
            "arm",
            2 / 16F,
            0F,
            0.5F - CORE_RADIUS,
            ConnectionState.CONNECTION, ConnectionState.INTERFACE
        )
                      .face(Direction.SOUTH, null))
        .withPart(new AxialPart<>(
            "interface",
            6 / 16F,
            0F,
            1 / 16F,
            ConnectionState.INTERFACE
        )
                      .face(Direction.SOUTH, f -> f.uvs(0, 0, 16, 16), true))
        .withPart(new AxialPart<>(
                      "disabled",
                      2.5F / 16F,
                      0.5F - 1 / 16F - CORE_RADIUS,
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    protected Property<ConnectionState> buildPropertyForDirection(Direction d) {
        return EnumProperty.create(d.getSerializedName(), ConnectionState.class);
    }

    @Override
    protected ConnectionState getDefaultPropertyValue(Direction d) {
        return ConnectionState.NONE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final LevelReader level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        BlockState state = defaultBlockState().setValue(
            WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        boolean disableAll = context.isSecondaryUseActive();
        for (Direction d : ALL_DIRECTIONS) {
            state = withConnectionState(
                state, d, disableAll
                          ? ConnectionState.DISABLED
                          : makeConnection(level, pos, d, pos.relative(d)));
        }
        return state;
    }

    private BlockState withConnectionState(BlockState bState, Direction dir, ConnectionState cState) {
        return getConnectionProp(dir).map(prop -> bState.setValue(prop, cState)).orElse(bState);
    }

    /**
     * Do not call if state is DISABLED
     */
    public ConnectionState makeConnection(LevelReader level, BlockPos thisPos, Direction dir, BlockPos neighborPos) {
        BlockEntity tile = level.getBlockEntity(neighborPos);
        if (tile != null && tile.getCapability(AAReference.NODE_CAPABILITY).isPresent()) {
            return ConnectionState.INTERFACE;
        }
        BlockState neighbor = level.getBlockState(neighborPos);
        return getConnectionProp(dir.getOpposite())
            .filter(prop -> neighbor.hasProperty(prop) && neighbor.getValue(prop).isNotDisabled())
            .map(prop -> ConnectionState.CONNECTION).orElse(ConnectionState.NONE);
    /*if(WIRE_BLOCKS.contains(neighbor.getBlock())) { //TODO: Convert to block tag
      return ConnectionState.CONNECTION;
    }*/
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(
        BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
        BlockPos currentPos, BlockPos neighborPos) {
        return getConnection(state, direction)
            .filter(ConnectionState::isNotDisabled)
            .map(
                conn -> withConnectionState(state, direction, makeConnection(level, currentPos, direction, neighborPos))
            ).orElseGet(() -> super.updateShape(state, direction, neighborState, level, currentPos, neighborPos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(
        BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult rayTraceResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.is(AATags.Items.MULTITOOL)) {
            // Multitool
            if (!level.isClientSide) {
                if (player.isCrouching()) {
                    level.removeBlock(pos, false);
                    if (!player.isCreative()) {
                        popResource(level, pos, new ItemStack(this));
                    }
                } else {
                    Vec3 start = new Vec3(player.xOld, player.yOld + player.getEyeHeight(), player.zOld);
                    Vec3 end = start.add(
                        player.getViewVector(0).scale(player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
                    Direction dir = rayTracePart(state, pos, start, end).direction();
                    if (dir == null) {
                        dir = rayTraceResult.getDirection();
                    }
                    level.setBlock(pos, getToggledState(state, level, pos, dir), Block.UPDATE_ALL);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, rayTraceResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private BlockState getToggledState(BlockState state, LevelReader level, BlockPos pos, Direction d) {
        return getConnection(state, d)
            .map(val -> withConnectionState(
                     state, d,
                     val.isNotDisabled() ? ConnectionState.DISABLED : makeConnection(level, pos, d, pos.relative(d))
                 )
            ).orElse(state);
    }
}
