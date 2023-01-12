package alpvax.advancedautocrafting.api.wire;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class PartHitResult<T extends IWirePart> {
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
    private final Direction faceHit;

    private PartHitResult(PartHitType hit, Direction direction, double distance2, T part, Direction faceHit) {
        this.hit = hit;
        this.direction = direction;
        this.distance2 = distance2;
        this.part = part;
        this.faceHit = faceHit;
    }

    static <T extends IWirePart>
    PartHitResult<T> hitArm(Direction direction, double distance2, T part, Direction faceHit) {
        return new PartHitResult<>(PartHitType.ARM, direction, distance2, part, faceHit);
    }
    static <T extends IWirePart>
    PartHitResult<T> hitPart(Direction direction, double distance2, T part, Direction faceHit) {
        return new PartHitResult<>(PartHitType.PART_BODY, direction, distance2, part, faceHit);
    }
    public static <T extends IWirePart>
    PartHitResult<T> hitCore(BlockHitResult ray, Vec3 start) {
        return hitCore(ray.getLocation().distanceTo(start), ray.getDirection());
    }
    public static <T extends IWirePart>
    PartHitResult<T> hitCore(double distance2, Direction faceHit) {
        return new PartHitResult<>(PartHitType.CORE, faceHit, distance2, null, faceHit);
    }
    public static <T extends IWirePart>
    PartHitResult<T> miss() {
        return new PartHitResult<>(PartHitType.MISS, null, Double.MAX_VALUE, null, null);
    }

    public Optional<T> getPart() {
        return wasMiss() ? Optional.empty() : Optional.of(part);
    }
    public Optional<Direction> getDirection() {
        return wasMiss() ? Optional.empty() : Optional.of(direction);
    }
    public double getDistanceSquared() {
        return distance2;
    }
    public Optional<Direction> getFaceHit() {
        return wasMiss() ? Optional.empty() : Optional.of(faceHit);
    }
    public Optional<VoxelShape> getShape() {
        return Optional.ofNullable(switch (hit) {
            case MISS, CORE -> null;
            case ARM -> IWirePart.BasicWireParts.ARM.getShape(direction);
            case PART_BODY -> part.getShape(direction);
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
