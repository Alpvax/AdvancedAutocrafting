package alpvax.advancedautocrafting.block.axial;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class AxialPart<T extends Comparable<T>> {
  public static final Direction[] ALL_DIRECTIONS = Direction.values();

  public final String name;
  public final float radius;
  public final float start;
  public final float end;
  public final Collection<T> allowedValues;
  Class<T> valueClass;

  private final Map<Direction, Consumer<BlockModelBuilder.ElementBuilder.FaceBuilder>> modelFaceModifiers = Util.make(
      Maps.newEnumMap(Direction.class),
      m -> {
        for(Direction d : ALL_DIRECTIONS) {
          m.put(d, f -> f.texture("#texture"));
        }
      }
  );

  private final Map<Direction, VoxelShape> shapes = Maps.newEnumMap(Direction.class);

  @SafeVarargs
  public AxialPart(String name, float radius, float start, float end, T... allowedValues) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Part name cannot be null (or \"\")");
    Preconditions.checkArgument(!name.equals("core"), "Part name cannot be \"core\")");
    this.name = name;
    this.radius = radius;
    this.start = start;
    this.end = end;
    this.allowedValues = ImmutableSet.copyOf(allowedValues);
    makeShapes();
  }

  public AxialPart<T> face(Direction d, @Nullable Consumer<BlockModelBuilder.ElementBuilder.FaceBuilder> faceBuilder) {
    return face(d, faceBuilder, false);
  }
  public AxialPart<T> face(Direction d, Consumer<BlockModelBuilder.ElementBuilder.FaceBuilder> faceBuilder, boolean additional) {
    if(faceBuilder == null) {
      modelFaceModifiers.remove(d);
    } else if(additional) {
      modelFaceModifiers.put(d, modelFaceModifiers.getOrDefault(d, f -> {}).andThen(faceBuilder));
    } else {
      modelFaceModifiers.put(d, faceBuilder);
    }
    return this;
  }

  public VoxelShape getShape(Direction d) {
    return shapes.get(d);
  }

  @SuppressWarnings("unchecked")
  public T[] getAllowedValues() {
    return allowedValues.toArray((T[])Array.newInstance(valueClass, 0));
  }

  private void makeShapes() {
    for (Direction d : ALL_DIRECTIONS) {
      float[] x = getMinMax(d.getStepX(), radius, start, end);
      float[] y = getMinMax(d.getStepY(), radius, start, end);
      float[] z = getMinMax(d.getStepZ(), radius, start, end);
      shapes.put(d, VoxelShapes.box(x[0], y[0], z[0], x[1], y[1], z[1]));
    }
  }

  /**
   * Call by BlockStateProvider to generate model file
   * @param modelBuilder generally `models().getBuilder("model_name")`
   * @return an ElementBuilder which can be further customised in the BlockStateProvider
   */
  /*
   * Only on Client
   */
  public BlockModelBuilder.ElementBuilder makeModelElement(BlockModelBuilder modelBuilder) {
    float r = radius * 16;
    BlockModelBuilder.ElementBuilder builder = modelBuilder
        .texture("particle", "#texture").element()
        .from(8 - r, 8 - r, start * 16)
        .to(8 + r, 8 + r, end * 16);
    modelFaceModifiers.forEach((d, mapper) -> mapper.accept(builder.face(d)));
    if(start == 0) {
      builder.face(Direction.NORTH).cullface(Direction.NORTH);
    }
    return builder;
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
      min = start;
      max = end;
    } else {
      min = 1F - end;
      max = 1F - start;
    }
    return new float[] {min, max};
  }
}
