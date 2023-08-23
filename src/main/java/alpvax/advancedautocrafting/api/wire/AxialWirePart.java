package alpvax.advancedautocrafting.api.wire;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public abstract class AxialWirePart implements IWirePart {
    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

    AxialWirePart() {
        for (var d : Direction.values()) {
            shapes.put(d, makeShapeForDir(d));
        }
    }
    protected abstract VoxelShape makeShapeForDir(Direction d);
}
