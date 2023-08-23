package alpvax.advancedautocrafting.api.wire;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PartHitResult<T extends IWirePart<T, D>, D extends INBTSerializable<?>> {
    public enum PartHitType {
        MISS,
        CORE,
        ARM,
        PART_BODY;
    }

    private final PartHitType hit;
    private final Direction direction;
    private final double distance2;
    private final T part;
    @Nullable
    private final D data;
    private final Direction faceHit;

    private PartHitResult(PartHitType hit, @Nullable Direction direction, double distance2, @Nullable T part, @Nullable D data, @Nullable Direction faceHit) {
        this.hit = hit;
        this.direction = direction;
        this.distance2 = distance2;
        this.part = part;
        this.data = data;
        this.faceHit = faceHit;
    }

    static <T extends IWirePart<T, D>, D extends INBTSerializable<?>>
    PartHitResult<T, D> hitArm(Direction direction, double distance2, T part, @Nullable D data, Direction faceHit) {
        return new PartHitResult<>(PartHitType.ARM, direction, distance2, part, data, faceHit);
    }
    static <T extends IWirePart<T, D>, D extends INBTSerializable<?>>
    PartHitResult<T, D> hitPart(Direction direction, double distance2, T part, @Nullable D data, Direction faceHit) {
        return new PartHitResult<>(PartHitType.PART_BODY, direction, distance2, part, data, faceHit);
    }
    public static <T extends IWirePart<T, D>, D extends INBTSerializable<?>>
    PartHitResult<T, D> hitCore(BlockHitResult ray, Vec3 start) {
        return hitCore(ray.getLocation().distanceTo(start), ray.getDirection());
    }
    public static <T extends IWirePart<T, D>, D extends INBTSerializable<?>>
    PartHitResult<T, D> hitCore(double distance2, Direction faceHit) {
        return new PartHitResult<>(PartHitType.CORE, faceHit, distance2, null, null, faceHit);
    }
    public static <T extends IWirePart<T, D>, D extends INBTSerializable<?>>
    PartHitResult<T, D> miss() {
        return new PartHitResult<>(PartHitType.MISS, null, Double.MAX_VALUE, null, null, null);
    }

    public Optional<T> getPart() {
        return wasMiss() ? Optional.empty() : Optional.ofNullable(part);
    }
    public Optional<Direction> getDirection() {
        return wasMiss() ? Optional.empty() : Optional.ofNullable(direction);
    }
    public double getDistanceSquared() {
        return distance2;
    }
    public Optional<Direction> getFaceHit() {
        return wasMiss() ? Optional.empty() : Optional.ofNullable(faceHit);
    }
    public Optional<VoxelShape> getShape() {
        return Optional.ofNullable(switch (hit) {
            case MISS, CORE -> null;
            case ARM -> IWirePart.ARM.get().getShape(direction);
            case PART_BODY -> part.getShape(data, direction);
        });
    }
    public boolean wasMiss() {
        return hit == PartHitType.MISS;
    }
    public boolean wasHit() {
        return hit != PartHitType.MISS;
    }
    public boolean hitArm() {
        return hit == PartHitType.ARM;
    }
    public boolean hitPart() {
        return hit == PartHitType.PART_BODY;
    }
}
