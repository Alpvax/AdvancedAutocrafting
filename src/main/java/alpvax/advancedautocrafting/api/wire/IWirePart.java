package alpvax.advancedautocrafting.api.wire;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.block.wire.WireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.RegistryObject;

public interface IWirePart<T extends IWirePart<T, D>, D extends INBTSerializable<?>> {
    RegistryObject<IWirePart<?, ?>> NONE = RegistryObject.createOptional(
        new ResourceLocation(AAReference.MODID, "none"),
        AAReference.WIRE_PARTS,
        AAReference.MODID
    );
    RegistryObject<ISimpleWirePart<?>> ARM = RegistryObject.createOptional(
        new ResourceLocation(AAReference.MODID, "arm"),
        AAReference.WIRE_PARTS,
        AAReference.MODID
    );

    /**
     * Whether wires running past this part can connect to this wire.
     * Should probably return false for most parts, unless they are supposed to be an inline part
     * @return true to allow connections through this part.
     */
    default boolean canConnect(D data) {
        return false;
    }

    @SuppressWarnings("unused")
    default void onAdded(D data, Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {}
    @SuppressWarnings("unused")
    default void onRemoved(D data, Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {}

    D initData();

    VoxelShape getShape(D data, Direction direction);

    String getModelKey(D data);

    /**
     * Whether the arm should be included in the rendering and raytrace of this part.
     * Should probably return true for parts which start not-attached to the core.
     */
    default ArmShapeInclusion includeArm(D data) {
        return ArmShapeInclusion.NONE;
    }

    /**
     * Perform a raytrace on this part for the given direction
     * @param direction the direction of this part
     * @param start the start position for the rayTrace
     * @param end the end position for the rayTrace
     * @param pos the position ofthe block this part is inside
     * @return a PartHitResult
     */
    default PartHitResult<T, D> rayTracePart(D data, Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        var shape = getShape(data, direction);
        BlockHitResult ray = null;
        switch (includeArm(data)) {
            case SINGLE_PART:
                ray = Shapes.or(shape, ARM.get().getShape(direction)).clip(start, end, pos);
                return ray == null ? PartHitResult.miss() : PartHitResult.hitPart(
                           direction,
                           ray.getLocation().distanceToSqr(start),
                           (T) this,
                           data,
                           ray.getDirection()
               );
            case SEPARATE:
                ray = ARM.get().getShape(direction).clip(start, end, pos);
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
                            (T) this,
                            data,
                            armRay.getDirection()
                        );
                    }
                }
                return PartHitResult.hitPart(
                    direction,
                    ray.getLocation().distanceToSqr(start),
                    (T) this,
                    data,
                    ray.getDirection()
                );
        }
        return PartHitResult.miss();
    }

    enum ArmShapeInclusion {
        /** Do not include the arm */
        NONE,
        /** Include the arm, but distinguish between arm or part in raytrace */
        SEPARATE,
        /** Include the arm as part of the part shape */
        SINGLE_PART;
    }
}
