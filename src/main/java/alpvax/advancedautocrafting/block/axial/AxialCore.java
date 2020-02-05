package alpvax.advancedautocrafting.block.axial;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class AxialCore {
    public final float radius;
    public final VoxelShape shape;

    public AxialCore(float radius) {
        this.radius = radius;
        float min = 0.5F - radius;
        float max = 0.5F + radius;
        shape = VoxelShapes.create(min, min, min, max, max, max);
    }
}