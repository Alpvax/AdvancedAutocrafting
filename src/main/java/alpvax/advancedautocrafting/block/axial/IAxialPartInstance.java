package alpvax.advancedautocrafting.block.axial;


import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface IAxialPartInstance<T extends Comparable<T>> {
  VoxelShape shape();
  Direction direction();

  class Impl<T extends Comparable<T>> implements IAxialPartInstance<T> {
    final AxialPart<T> part;
    final Direction direction;
    final VoxelShape shape;

    public Impl(AxialPart<T> part, Direction dir) {
      this.part = part;
      direction = dir;
      shape = part.getShape(dir);
    }

    @Override
    public VoxelShape shape() {
      return shape;
    }

    @Override
    public Direction direction() {
      return direction;
    }
  }

  class AxialCore<T extends Comparable<T>> implements IAxialPartInstance<T> {
    public final float radius;
    private final VoxelShape shape;

    public AxialCore(float radius) {
      this.radius = radius;
      float min = 0.5F - radius;
      float max = 0.5F + radius;
      shape = Shapes.box(min, min, min, max, max, max);
    }

    public static <T extends Comparable<T>> IAxialPartInstance<T> from(AxialBlockShape<T> blockShape) {
      return new AxialCore<T>(blockShape.coreRadius);
    }

    @Override
    public VoxelShape shape() {
      return shape;
    }

    @Override
    public Direction direction() {
      return null;
    }
  }
}
