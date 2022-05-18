package alpvax.advancedautocrafting.block.axial;

import alpvax.advancedautocrafting.init.AAItems;
import alpvax.advancedautocrafting.init.AATags;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AxialBlock<T extends Comparable<T>> extends Block {
    public static final Direction[] ALL_DIRECTIONS = Direction.values();
    private final AxialBlockShape<T> shape;
    private Map<Direction, Property<T>> directionToPropertyMap;

    public AxialBlock(Properties properties, AxialBlockShape<T> shape) {
        super(properties);
        this.shape = shape;
        BlockState state = getStateDefinition().any();
        for (Direction d : ALL_DIRECTIONS) {
            Property<T> prop = directionToPropertyMap.get(d);
            if (prop != null) {
                state = state.setValue(prop, getDefaultPropertyValue(d));
            }
        }
        registerDefaultState(state);
    }

    public void forEachDirection(BiConsumer<Direction, Property<T>> consumer) {
        directionToPropertyMap.forEach(consumer);
    }

    public Optional<Property<T>> getConnectionProp(Direction d) {
        return Optional.ofNullable(directionToPropertyMap.get(d));
    }

    public Optional<T> getConnection(BlockState state, Direction d) {
        return getConnectionProp(d).map(prop -> state.hasProperty(prop) ? state.getValue(prop) : null);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        directionToPropertyMap = Util.make(Maps.newEnumMap(Direction.class), map -> {
            for (Direction d : ALL_DIRECTIONS) {
                Property<T> prop = buildPropertyForDirection(d);
                if (prop != null) {
                    map.put(d, prop);
                    builder.add(prop);
                }
            }
        });
    }

    @Nullable
    protected abstract Property<T> buildPropertyForDirection(Direction d);

    protected abstract T getDefaultPropertyValue(Direction d);

    public AxialBlockShape<T> getBlockShape() {
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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
                        e.getViewVector(0).scale(ForgeMod.REACH_DISTANCE.get().sanitizeValue(Double.MAX_VALUE)));
                    return getPartialBlockHighlight(state, rayTracePart(state, pos, start, end));
                }
            }
        }
        Map<Direction, T> values = Maps.newEnumMap(Direction.class);
        directionToPropertyMap.forEach((d, prop) -> values.put(d, state.getValue(prop)));
        return shape.getCombinedShape(values);
    }

    public IAxialPartInstance<T> rayTracePart(BlockState state, BlockPos pos, Vec3 start, Vec3 end) {
        Direction dir = null;
        AxialPart<T> part = null;
        BlockHitResult ray = shape.getCoreShape().clip(start, end, pos);
        double dSquared = ray == null ? Double.MAX_VALUE : ray.getLocation().distanceToSqr(start);
        for (Direction d : ALL_DIRECTIONS) {
            Optional<Property<T>> prop = getConnectionProp(d);
            if (prop.isPresent()) {
                for (AxialPart<T> p : shape.validParts(state.getValue(prop.get())).collect(Collectors.toList())) {
                    ray = p.getShape(d).clip(start, end, pos);
                    if (ray != null) {
                        double d2 = ray.getLocation().distanceToSqr(start);
                        if (d2 < dSquared) {
                            dSquared = d2;
                            dir = d;
                            part = p;
                        }
                    }
                }
            }
        }
        return dir == null ? IAxialPartInstance.AxialCore.from(shape) : new IAxialPartInstance.Impl<>(part, dir);
    }

    protected VoxelShape getPartialBlockHighlight(BlockState state, IAxialPartInstance<T> partInstance) {
        Direction d = partInstance.direction();
        return getConnection(state, d).map(val -> getBlockShape().getAxialShape(d, val))
            .orElse(getBlockShape().getCoreShape());
        //return partInstance.shape();
    }
}
