package alpvax.advancedautocrafting.block.axial;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AxialBlockShape<T extends Comparable<T>> {
  /** if > 0, will produce a cube of that radius as the core */
  protected float coreRadius = -1;
  /** the core shape */
  protected VoxelShape coreShape = VoxelShapes.empty();
  /** the texture to use for the core */
  protected ResourceLocation coreTexture;

  protected Map<String, AxialPart<T>> parts = new HashMap<>();

  public VoxelShape getCombinedShape(Map<Direction, T> propertyValues) {
    List<VoxelShape> shapes = new ArrayList<>();
    propertyValues.forEach((d, v) -> {
      parts.values().stream()
          .filter(p -> p.allowedValues.contains(v))
          .forEach(p -> {
            shapes.add(p.getShape(d));
          });
    });
    return VoxelShapes.or(coreShape, shapes.toArray(new VoxelShape[0]));
  }

  @OnlyIn(Dist.CLIENT)
  public ModelFile buildCorePart(
      BlockModelBuilder modelBuilder,
      BiConsumer<Direction, BlockModelBuilder.ElementBuilder.FaceBuilder> faceMapper
  ) {
    float min = 0.5F - coreRadius;
    float max = 0.5F + coreRadius;
    return modelBuilder
        .texture("particle", "#texture").element()
        .from(min, min, min)
        .to(max, max, max)
        .allFaces((d, f) -> {
          f.texture("#texture");
          faceMapper.accept(d, f);
        })
        .end();
  }

  public static <T extends Comparable<T>> Builder<T> builder() {
    return new Builder<T>();
  }

  public static class Builder<T extends Comparable<T>> extends AxialBlockShape<T> {
    public Builder<T> withCore(float radius, ResourceLocation texture) {
      coreRadius = radius;
      coreTexture = texture;
      double min = 0.5 - radius;
      double max = 0.5 + radius;
      coreShape = VoxelShapes.create(min, min, min, max, max, max);
      return this;
    }

    public Builder<T> withPart(String name, float radius, float start, float end, T... allowedValues) {
      return withPart(new AxialPart<T>(name, radius, start, end, allowedValues));
    }
    public Builder<T> withPart(AxialPart<T> part) {
      parts.put(part.name, part);
      return this;
    }

    public AxialBlockShape<T> build() {
      return this;
    }
  }
}
