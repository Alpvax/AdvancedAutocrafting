package alpvax.advancedautocrafting.block.wire;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class WireShape<T extends IStringSerializable> {
  private final VoxelShape core;
  private final Map<Pair<T, Direction>, VoxelShape> parts;

  private WireShape(VoxelShape core, Map<Pair<T, Direction>, VoxelShape> parts) {
    this.core = core;
    this.parts = parts;
  }

  public VoxelShape getCore() {
    return core;
  }
  public VoxelShape getFor(T value, Direction direction) {
    return parts.getOrDefault(Pair.of(value, direction), VoxelShapes.empty());
  }
  public VoxelShape getComplete(Map<Direction, T> values) {
    return values.entrySet().stream()
               .map(e -> getFor(e.getValue(), e.getKey()))
               .reduce(getCore(), VoxelShapes::or);
  }

  public Pair<VoxelShape, Direction> rayTracePart(Map<Direction, T> values, BlockPos pos, Vector3d start, Vector3d end) {
    Pair<Double, Direction> coreData = Optional.ofNullable(getCore().rayTrace(start, end, pos))
                                           .map(ray -> Pair.of(
                                               ray.getHitVec().squareDistanceTo(start),
                                               ray.getFace()
                                           )).orElse(Pair.of(Double.MAX_VALUE, null));
    return values.entrySet().stream()
        .map(e -> Pair.of(e.getKey(), getFor(e.getValue(), e.getKey())))
        .filter(e -> !e.getRight().isEmpty())
        .map(e -> Triple.of(
            Optional.ofNullable(e.getRight().rayTrace(start, end, pos)).map(ray -> ray.getHitVec().squareDistanceTo(start)).orElse(Double.MAX_VALUE),
            e.getLeft(),
            e.getRight()
        ))
        .sorted()
        .findFirst()
        .map(closest -> closest.getLeft() < coreData.getLeft() ? Pair.of(closest.getRight(), closest.getMiddle()) : null)
        .orElseGet(() -> Pair.of(core, coreData.getRight()));
  }

  public static class Builder<T extends IStringSerializable> {
    private final Map<String, Values> parts = new HashMap<>();
    private final Multimap<T, String> valueMap = HashMultimap.create();
    private float coreRadius;
    private final boolean pixels;

    /**
     * @param usePixelValues If true, use values 0-16F instead of 0-1F to define parts
     */
    public Builder(boolean usePixelValues) {
      pixels = usePixelValues;
    }

    public Builder<T> withCore(float coreRadius) {
      this.coreRadius = checkAndFormatValue(coreRadius, "coreRadius");
      return this;
    }

    public Builder<T> withPartDef(String name, float radius, float start, float end) {
      parts.put(name, new Values(radius, start, end));
      return this;
    }
    public Builder<T> withPart(T value, float radius, float start, float end) {
      String name = value.func_176610_l();
      parts.put(name, new Values(radius, start, end));
      valueMap.put(value, name);
      return this;
    }
    public Builder<T> includePart(T value, String... partNames) {
      for (String name : partNames) {
        valueMap.put(value, name);
      }
      return this;
    }

    public WireShape<T> build() {
      float cr0 = 0.5F - coreRadius;
      float cr1 = 0.5F + coreRadius;
      Map<Pair<String, Direction>, VoxelShape> built = new HashMap<>();
      return new WireShape<>(
          VoxelShapes.create(cr0, cr0, cr0, cr1, cr1, cr1),
          valueMap.asMap().entrySet().stream()
              .flatMap(e -> Arrays.stream(Direction.values()).map(d -> Pair.of(
                  Pair.of(e.getKey(), d),
                  e.getValue().stream().map(partName -> built.computeIfAbsent(
                      Pair.of(partName, d),
                      $ -> {
                        Values v = parts.get(partName);
                        return v == null ? null : v.makeShape(d);
                      }
                  ))
                      .filter(Objects::nonNull)
                      .reduce(VoxelShapes::or)
              )))
              .filter(e -> e.getValue().isPresent())
              .collect(Collectors.toMap(Pair::getKey, e -> e.getValue().get()))
      );
    }

    private float checkAndFormatValue(float val, String name) {
      String sval = Float.toString(val);
      if (pixels) {
        val /= 16F;
      }
      Preconditions.checkArgument(0F <= val && val <= 0.5F, "%s must be within the range 0 - %s; Recieved: %s", name, (pixels ? "8" : "0.5"), sval);
      return val;
    }

    private class Values {
      float radius;
      float start;
      float end;
      private Values(float radius, float start, float end) {
        this.radius = checkAndFormatValue(radius, "radius");
        this.start = 0.5F - checkAndFormatValue(start, "start");
        this.end = 0.5F - checkAndFormatValue(end, "end");
      }
      private VoxelShape makeShape(Direction d) {
        float r = radius;
        float r0 = 0.5F - r;
        float r1 = 0.5F + r;
        // Min/max taken care of in AABB constructor
        IntFunction<float[]> f = (i) -> i == 0 ? new float[]{r0, r1} : new float[]{0.5F + start * i, 0.5F + end * i};
        float[] x = f.apply(d.getXOffset());
        float[] y = f.apply(d.getYOffset());
        float[] z = f.apply(d.getZOffset());
        return VoxelShapes.create(x[0], y[0], z[0], x[1], y[1], z[1]);
      }
    }
  }
}
