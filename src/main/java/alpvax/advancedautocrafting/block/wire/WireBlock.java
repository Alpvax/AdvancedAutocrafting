package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.api.wire.PartHitResult;
import alpvax.advancedautocrafting.init.AABlocks;
import alpvax.advancedautocrafting.init.AAItems;
import alpvax.advancedautocrafting.init.AATags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class WireBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final float CORE_RADIUS =  3 / 16F;

    private static final VoxelShape CORE_SHAPE = Shapes.box(
        0.5 - CORE_RADIUS, 0.5 - CORE_RADIUS,0.5 - CORE_RADIUS,
        0.5 + CORE_RADIUS,0.5 + CORE_RADIUS,0.5 + CORE_RADIUS
    );

    public WireBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        //TODO: set initial state
        return new WireBlockEntity(pPos, pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final var level = context.getLevel();
        final var pos = context.getClickedPos();
        return defaultBlockState()
            .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        /*TODO: all disabled when sneak-placed
        var disableAll = context.isSecondaryUseActive();
        for (var d : Direction.values()) {
            state = withConnectionState(
                state, d, disableAll
                          ? alpvax.advancedautocrafting.block.WireBlock.ConnectionState.DISABLED
                          : makeConnection(level, pos, d, pos.relative(d)));
        }
        return state;
        */
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, state.getBlock(), 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        super.tick(state, level, pos, rand);
        withBlockEntity(level, pos, be -> {
            for (Direction d : Direction.values()) {
                be.updateConnection(d, level.getBlockEntity(pos.relative(d)));
            }
        });
    }

    private void withBlockEntity(LevelAccessor level, BlockPos pos, Consumer<WireBlockEntity> func) {
        WireBlockEntity be = AABlocks.Entities.WIRE.get().getBlockEntity(level, pos);
        if (be != null) {
            func.accept(be);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
        @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player,
        @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        var stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.is(AATags.Items.MULTITOOL)) {
            // Multitool
            if (!level.isClientSide) {
                if (player.isCrouching()) {
                    level.removeBlock(pos, false);
                    if (!player.isCreative()) {
                        popResource(level, pos, new ItemStack(this));
                    }
                } else {
                    var start = new Vec3(player.xOld, player.yOld + player.getEyeHeight(), player.zOld);
                    //noinspection ConstantConditions
                    var end = start.add(
                        player.getViewVector(0).scale(player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
                    var dir = rayTracePart(level, pos, start, end)
                                  .getDirection()
                                  .orElseGet(rayTraceResult::getDirection);
                    withBlockEntity(level, pos, be -> be.toggleDisabled(dir));
                }
            }
            return InteractionResult.SUCCESS;
        } else if (stack.isEmpty()) {
            player.sendSystemMessage(Component.literal(getDebugConnections(level, pos)));//XXX
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, rayTraceResult);
    }

    //XXX
    private String getDebugConnections(Level level, BlockPos pos) {//XXX
        String side = "[" + (level.isClientSide ? "CLIENT" : "SERVER") + "]";
        StringBuilder sb = new StringBuilder(side);
        WireBlockEntity tile = AABlocks.Entities.WIRE.get().getBlockEntity(level, pos);
        if (tile != null) {
            for (Direction d : Direction.values()) {
                sb.append("  ").append(d.getName()).append(": ").append(tile.getPart(d).getName()).append("\n");
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(
        @NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
        @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (!level.isClientSide()) {
            WireBlockEntity be = AABlocks.Entities.WIRE.get().getBlockEntity(level, currentPos);
            if (be != null) {
                be.updateConnection(direction, level.getBlockEntity(neighborPos));
            }
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
        @NotNull BlockState state, @NotNull BlockGetter level,
        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext ctx) {
            Entity e = ctx.getEntity();
            if (e != null) {
                if (context.isHoldingItem(AAItems.MULTITOOL.get())
                    || e instanceof Player player && StreamSupport
                         .stream(player.getHandSlots().spliterator(), true)
                         .anyMatch(stack -> stack.is(AATags.Items.MULTITOOL))
                ) {
                    Vec3 start = new Vec3(e.xOld, e.yOld + e.getEyeHeight(), e.zOld);
                    Vec3 end = start.add(
                        e.getViewVector(0)
                            .scale(ForgeMod.REACH_DISTANCE.get().sanitizeValue(Double.MAX_VALUE)));
                    return rayTracePart(level, pos, start, end).getShape().orElse(CORE_SHAPE);
                }
            }
        }
        var be = AABlocks.Entities.WIRE.get().getBlockEntity(level, pos);
        if (be != null) {
            return Arrays.stream(Direction.values()).map(d -> be.getPart(d).getShape(d)).reduce(CORE_SHAPE, Shapes::or);
        }
        return CORE_SHAPE;
    }

    public PartHitResult<?> rayTracePart(BlockGetter level, BlockPos pos, Vec3 start, Vec3 end) {
        var ray = CORE_SHAPE.clip(start, end, pos);
        var result = ray == null ? PartHitResult.miss() : PartHitResult.hitCore(ray, start);
        var be = AABlocks.Entities.WIRE.get().getBlockEntity(level, pos);
        if (be != null) {
            var dSquared = ray == null ? Double.MAX_VALUE : ray.getLocation().distanceToSqr(start);
            for (var d : Direction.values()) {
                IWirePart part = be.getPart(d);
                var hit = part.rayTracePart(d, start, end, pos);
                var d2 = hit.getDistanceSquared();
                if (hit.wasHit() && d2 < dSquared) {
                    dSquared = d2;
                    result = hit;
                }
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
