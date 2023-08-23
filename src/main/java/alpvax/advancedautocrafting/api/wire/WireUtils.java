package alpvax.advancedautocrafting.api.wire;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.IntFunction;

import static alpvax.advancedautocrafting.api.AAReference.Wire.CORE_RADIUS;

public class WireUtils {
    /**
     * Create a Block box shape along an axis, with the origin at the centre of the block,
     * where the start distance is
     * {@link alpvax.advancedautocrafting.api.AAReference.Wire#CORE_RADIUS WireBlock#CORE_RADIUS}
     * (i.e. the box is attached to the core of the wire)
     * <br><br>
     * See {@link #makeAxialShape(Direction, float, float, float)}
     * @param direction The direction to point along
     * @param radius the distance from the axis in the other two directions (range [0 - 0.5])
     * @param endLength the end distance from the centre of the block (range [0 - 0.5],
     *                  or [-0.5 - 0] to appear to be attached to the opposite side)
     * @return a single box VoxelShape
     */
    public static VoxelShape makeAxialShape(Direction direction, float radius, float endLength) {
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
    public static VoxelShape makeAxialShape(Direction direction, float radius, float fromLength, float toLength) {
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
}
