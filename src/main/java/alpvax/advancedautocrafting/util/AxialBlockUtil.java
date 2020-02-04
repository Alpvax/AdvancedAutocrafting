package alpvax.advancedautocrafting.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AxialBlockUtil {
  public static final Direction[] ALL_DIRECTIONS = Direction.values();

  public static class AxialPart {
    public final String name;
    public final float radius;
    public final float start;
    public final float end;

    private final Map<Direction, VoxelShape> shapes = new HashMap<>();

    public AxialPart(String name, float radius, float start, float end) {
      this.name = name;
      this.radius = radius;
      this.start = start;
      this.end = end;
    }

    private void makeShapes() {
      for (Direction d : ALL_DIRECTIONS) {
        float[] x = getMinMax(d.getXOffset(), radius, start, end);
        float[] y = getMinMax(d.getYOffset(), radius, start, end);
        float[] z = getMinMax(d.getZOffset(), radius, start, end);
        shapes.put(d, VoxelShapes.create(x[0], y[0], z[0], x[1], y[1], z[1]));
      }
    }

    @OnlyIn(Dist.CLIENT)
    public ModelBuilder.ElementBuilder makeModelfile(BlockModelBuilder modelBuilder, Direction... ignoredFaces) {
      Set<Direction> dirs = Set.of(ignoredFaces);
      ModelBuilder.ElementBuilder builder = modelBuilder
          .texture("particle", "#texture").element()
              .from(8 - radius, 8 - radius, start)
              .to(8 + radius, 8 + radius, end);
      for(Direction d : ALL_DIRECTIONS) {
        if(dirs.contains(d)) continue;
        builder.face(d).texture("#texture");
      }
      if(start == 0) {
        builder.face(Direction.NORTH).cullface(Direction.NORTH);
      }
      return builder;
    }
  }

  /**
   * @param axisValue the return value of Direction.getAXISOffset()
   * @param radius the radius of the part (should not exceed 0.5)
   * @param start the distance from the edge of the block (should not exceed end)
   * @param end the distance from the edge of the block (should not exceed 0.5)
   * @return [min, max] co-ords for the given axis (range 0-1)
   */
  private static float[] getMinMax(int axisValue, float radius, float start, float end) {
    float min;
    float max;
    if (axisValue == 0) {
      min = 0.5F - radius;
      max = 0.5F + radius;
    } else if (axisValue < 0) {
      min = 0.5F - start - end;
      max = 0.5F - end;
    } else {
      min = 0.5F + end;
      max = 0.5F + end + start;
    }
    return new float[] {min, max};
  }
}
