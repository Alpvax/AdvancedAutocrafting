package alpvax.advancedautocrafting.block.wire.parts;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.api.wire.PartHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.Function;

import static alpvax.advancedautocrafting.api.AAReference.Wire.CORE_RADIUS;
import static alpvax.advancedautocrafting.api.wire.WireUtils.makeAxialShape;

public class None implements IWirePart<None, None.Connection> {
    public enum Connection implements INBTSerializable<StringTag> {
        NONE,
        ARM(2F / 16, 0.5F),
        DISABLED(2.5F / 16, CORE_RADIUS + 1F / 16),
        BLOCK_INTERFACE(d -> makeAxialShape(d, 6F / 16, 0.5F - 1F / 16, 0.5F));

        private final EnumMap<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        Connection() {
            this(d -> Shapes.empty());
        }
        Connection(float radius, float length) {
            this(d -> makeAxialShape(d, radius, length));
        }
        Connection(Function<Direction, VoxelShape> shapeFactory) {
            for (var d : Direction.values()) {
                shapes.put(d, shapeFactory.apply(d));
            }
        }
        @Override
        public StringTag serializeNBT() {
            return StringTag.valueOf(name().toLowerCase());
        }
        @Override
        public void deserializeNBT(StringTag nbt) {
            valueOf(nbt.getAsString().toUpperCase());
        }
    }

    @Override
    public VoxelShape getShape(Connection data, @NotNull Direction direction) {
        return data.shapes.get(direction);
    }
    
    @Override
    public String getModelKey(Connection data) {
        return switch (data) {
            case NONE -> "none";
            case ARM -> "arm";
            case DISABLED -> "disabled";
            case BLOCK_INTERFACE -> "interface";
        };
    }
    @Override
    public boolean canConnect(Connection data) {
        return data != Connection.DISABLED;
    }
    @Override
    public Connection initData() {
        return Connection.NONE;
    }
    @Override
    public ArmShapeInclusion includeArm(Connection data) {
        return switch (data) {
            case NONE, ARM, DISABLED -> ArmShapeInclusion.NONE;
            case BLOCK_INTERFACE -> ArmShapeInclusion.SINGLE_PART;
        };
    }
    @Override
    public PartHitResult<None, Connection> rayTracePart(Connection data, Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        return PartHitResult.miss();
    }

    public Connection calculateConnection(Connection data, Direction direction, @Nullable BlockEntity neighbourBlockEntity, boolean ignoreDisabled) {
        if (ignoreDisabled || canConnect(data)) {
            if (neighbourBlockEntity != null) {
                var cap = neighbourBlockEntity
                              .getCapability(AAReference.NODE_CAPABILITY, direction.getOpposite());
                //TODO: connect to adjacent node: cap.map(n -> n.connectTo(this))
                if (cap.isPresent()) {
                    return Connection.ARM;
                } else if (!neighbourBlockEntity.getCapability(AAReference.NODE_CAPABILITY, null).isPresent()) {
                    //} else if (/*TODO: should connect capabilities?*/) {
                    return Connection.BLOCK_INTERFACE;
                }
            }
        }
        return Connection.NONE;
    }
}
