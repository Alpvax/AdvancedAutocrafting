package alpvax.advancedautocrafting.api.wire;

import alpvax.advancedautocrafting.block.wire.WireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public interface ISimpleWirePart<T extends IWirePart<T, ISimpleWirePart.BlankData>> extends IWirePart<T, ISimpleWirePart.BlankData> {
    class BlankData implements INBTSerializable<StringTag> {
        private static final BlankData INSTANCE = new BlankData();
        @Override
        public StringTag serializeNBT() {
            return StringTag.valueOf("");
        }
        @Override
        public void deserializeNBT(StringTag nbt) {}
    }


    /**
     * Whether wires running past this part can connect to this wire.
     * Should probably return false for most parts, unless they are supposed to be an inline part
     * @return true to allow connections through this part.
     */
    default boolean canConnect() {
        return IWirePart.super.canConnect(BlankData.INSTANCE);
    }
    @Override
    default boolean canConnect(BlankData data) {
        return canConnect();
    }

    @SuppressWarnings("unused")
    default void onAdded(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        IWirePart.super.onAdded(BlankData.INSTANCE, level, position, direction, blockEntity);
    }
    @Override
    default void onAdded(BlankData data, Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        onAdded(level, position, direction, blockEntity);
    }

    default void onRemoved(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        IWirePart.super.onRemoved(BlankData.INSTANCE, level, position, direction, blockEntity);
    }
    @Override
    @SuppressWarnings("unused")
    default void onRemoved(BlankData data, Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        onRemoved(level, position, direction, blockEntity);
    }

    @Override
    default BlankData initData() {
        return BlankData.INSTANCE;
    }

    VoxelShape getShape(@NotNull Direction direction);
    @Override
    default VoxelShape getShape(BlankData data, @NotNull Direction direction) {
        return getShape(direction);
    }

    /**
     * Whether the arm should be included in the rendering and raytrace of this part.
     * Should probably return true for parts which start not-attached to the core.
     */
    default ArmShapeInclusion includeArm() {
        return IWirePart.super.includeArm(BlankData.INSTANCE);
    }
    @Override
    default ArmShapeInclusion includeArm(BlankData data) {
        return includeArm();
    }

    /**
     * Perform a raytrace on this part for the given direction
     * @param direction the direction of this part
     * @param start the start position for the rayTrace
     * @param end the end position for the rayTrace
     * @param pos the position ofthe block this part is inside
     * @return a PartHitResult
     */
    default PartHitResult<T, BlankData> rayTracePart(Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        return IWirePart.super.rayTracePart(BlankData.INSTANCE, direction, start, end, pos);
    }
    @Override
    default PartHitResult<T, BlankData> rayTracePart(BlankData data, Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        return rayTracePart(direction, start, end, pos);
    }
}
