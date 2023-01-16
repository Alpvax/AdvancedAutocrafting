package alpvax.advancedautocrafting.api.wire;

import alpvax.advancedautocrafting.block.wire.WireBlockEntity;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.IntFunction;

import static alpvax.advancedautocrafting.block.wire.WireBlock.CORE_RADIUS;

public interface IWirePart {
    /**
     * Whether wires running past this part can connect to this wire.
     * Returns false for {@link BasicWireParts#DISABLED}, and true for all other basic parts.
     * Should probably return false for all other parts, unless they are supposed to be an inline part
     * @return true to allow connections through this part.
     */
    default boolean canConnect() {
        return false;
    }

    @SuppressWarnings("unused")
    default void onAdded(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {}
    @SuppressWarnings("unused")
    default void onRemoved(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {}

    VoxelShape getShape(@NotNull Direction direction);

    /**
     * Whether the arm should be included in the rendering and raytrace of this part.
     * Should probably return true for parts which start not-attached to the core.
     */
    default ArmShapeInclusion includeArm() {
        return ArmShapeInclusion.NONE;
    }

    /**
     * Perform a raytrace on this part for the given direction
     * @param <T> this part type
     * @param direction the direction of this part
     * @param start the start position for the rayTrace
     * @param end the end position for the rayTrace
     * @param pos the position ofthe block this part is inside
     * @return a PartHitResult
     */
    default <T extends IWirePart> PartHitResult<T> rayTracePart(Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        var shape = getShape(direction);
        //noinspection unchecked
        var thisT = (T) this;
        BlockHitResult ray = null;
        switch (includeArm()) {
            case SINGLE_PART:
                ray = Shapes.or(shape, BasicWireParts.ARM.getShape(direction)).clip(start, end, pos);
                return ray == null ? PartHitResult.miss() : PartHitResult.hitPart(
                           direction,
                           ray.getLocation().distanceToSqr(start),
                           thisT,
                           ray.getDirection()
               );
            case SEPARATE:
                ray = BasicWireParts.ARM.getShape(direction).clip(start, end, pos);
                // Continue on to the case where the arm is ignored
            case NONE:
                var armRay = ray;
                ray = shape.clip(start, end, pos);
                if (ray == null && armRay == null) {
                    return PartHitResult.miss();
                } else if (armRay != null) {
                    var armD2 = armRay.getLocation().distanceToSqr(start);
                    if (ray == null || armD2 < ray.getLocation().distanceToSqr(start)) {
                        return PartHitResult.hitArm(
                            direction,
                            armD2,
                            thisT,
                            armRay.getDirection()
                        );
                    }
                }
                return PartHitResult.hitPart(
                    direction,
                    ray.getLocation().distanceToSqr(start),
                    thisT,
                    ray.getDirection()
                );
        }
        return PartHitResult.miss();
    }


    /**
     * Create a Block box shape along an axis, with the origin at the centre of the block,
     * where the start distance is
     * {@link alpvax.advancedautocrafting.block.wire.WireBlock#CORE_RADIUS WireBlock#CORE_RADIUS}
     * (i.e. the box is attached to the core of the wire)
     * <br><br>
     * See {@link #makeAxialShape(Direction, float, float, float)}
     * @param direction The direction to point along
     * @param radius the distance from the axis in the other two directions (range [0 - 0.5])
     * @param endLength the end distance from the centre of the block (range [0 - 0.5],
     *                  or [-0.5 - 0] to appear to be attached to the opposite side)
     * @return a single box VoxelShape
     */
    static VoxelShape makeAxialShape(Direction direction, float radius, float endLength) {
        return makeAxialShape(direction, radius, CORE_RADIUS, endLength);
    }
    /**
     * Create a Block box shape centered on an axis, with the origin at the centre of the block
     * @param direction The direction to point along
     * @param radius the distance from the axis in the other two directions (range [0 - 0.5])
     * @param fromLength the start distance from the centre of the block (range [0 - 0.5],
     *                   or [-0.5 - 0] to appear to be attached to the opposite side)
     * @param toLength the end distance from the centre of the block (range [0 - 0.5],
     *                 or [-0.5 - 0] to appear to be attached to the opposite side)
     *                 MUST BE > fromLength
     * @return a single box VoxelShape
     */
    static VoxelShape makeAxialShape(Direction direction, float radius, float fromLength, float toLength) {
        Preconditions.checkArgument(radius > 0 && radius <= 0.5F, "radius must be in range (0, 0.5]");
        Preconditions.checkArgument(fromLength >= -0.5 && fromLength <= 0.5F, "fromLength must be in range (0, 0.5]");
        Preconditions.checkArgument(toLength >= -0.5 && toLength <= 0.5F, "toLength must be in range (0, 0.5]");
        Preconditions.checkArgument(toLength > fromLength, "toLength must be greater than fromLength");
        IntFunction<float[]> f = i -> i == 0
            ? new float[] { 0.5F - radius, 0.5F + radius }
            : direction.getAxisDirection() == Direction.AxisDirection.POSITIVE
              ? new float[] { 0.5F + i * fromLength, 0.5F + i * toLength }
              : new float[] { 0.5F + i * toLength, 0.5F + i * fromLength };
        var norm = direction.getNormal();
        var x = f.apply(norm.getX());
        var y = f.apply(norm.getY());
        var z = f.apply(norm.getZ());
        return Shapes.box(x[0], y[0], z[0], x[1], y[1], z[1]);
    }
    String getName();//XXX

    enum ArmShapeInclusion {
        /** Do not include the arm */
        NONE,
        /** Include the arm, but distinguish between arm or part in raytrace */
        SEPARATE,
        /** Include the arm as part of the part shape */
        SINGLE_PART;
    }

    enum BasicWireParts implements IWirePart {
        NONE,
        ARM(2F / 16, 0.5F),
        DISABLED(2.5F / 16, CORE_RADIUS + 1F / 16),
        BLOCK_INTERFACE(d -> makeAxialShape(d, 6F / 16, 0.5F - 1F / 16, 0.5F));

        private final EnumMap<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        BasicWireParts() {}
        BasicWireParts(float radius, float length) {
            this(d -> makeAxialShape(d, radius, length));
        }
        BasicWireParts(Function<Direction, VoxelShape> shapeFactory) {
            for (var d : Direction.values()) {
                shapes.put(d, shapeFactory.apply(d));
            }
        }

        @Override
        public VoxelShape getShape(@NotNull Direction direction) {
            return shapes.computeIfAbsent(direction, d -> Shapes.empty());
        }

        @Override
        public boolean canConnect() {
            return this != DISABLED;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
