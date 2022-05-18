package alpvax.advancedautocrafting.block.axial;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AxialBlockShape<T extends Comparable<T>> {
    protected final String modelName;
    /**
     * if > 0, will produce a cube of that radius as the core
     */
    protected float coreRadius = -1;
    /**
     * the core shape
     */
    protected VoxelShape coreShape = Shapes.empty();
    protected Map<String, AxialPart<T>> parts = new HashMap<>();
    /*
     * Only on Client
     */
    private Map<String, ModelFile> models;

    private AxialBlockShape(String modelName) {
        this.modelName = modelName;
    }

    public static <T extends Comparable<T>> Builder<T> builder(String modelName, Class<T> valueClass) {
        return new Builder<T>(modelName, valueClass);
    }

    public VoxelShape getCombinedShape(Map<Direction, T> propertyValues) {
        List<VoxelShape> shapes = new ArrayList<>();
        propertyValues.forEach((d, v) -> {
            shapes.add(getAxialShape(d, v));
        });
        return Shapes.or(getCoreShape(), shapes.toArray(new VoxelShape[0]));
    }

    public VoxelShape getCoreShape() {
        return coreShape;
    }

    public VoxelShape getAxialShape(Direction d, T propertyValue) {
        return Shapes.or(
            Shapes.empty(),
            validParts(propertyValue).map(p -> p.getShape(d)).toArray(VoxelShape[]::new)
        );
    }

    /*
     * Only on Client
     */
    public ModelFile buildCorePart(
        BlockModelBuilder modelBuilder,
        BiConsumer<Direction, BlockModelBuilder.ElementBuilder.FaceBuilder> faceMapper
    ) {
        float min = (0.5F - coreRadius) * 16;
        float max = (0.5F + coreRadius) * 16;
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

    /*
     * Only on Client
     */
    public Map<String, ModelFile> buildBlockModelParts(String path, BlockModelProvider modelProvider) {
        if (models == null) {
            models = new HashMap<>();
            String p = path + modelName;
            models.put("core", buildCorePart(modelProvider.getBuilder(p + "_core"), (d, f) -> f.uvs(0, 0, 16, 16)));
            parts.forEach((name, part) -> {
                models.put(name, part.makeModelElement(modelProvider.getBuilder(p + "_" + name)).end());
            });
        }
        return models;
    }

    public void forEach(Consumer<AxialPart<T>> consumer) {
        parts.values().forEach(consumer);
    }

    public Stream<AxialPart<T>> validParts(T propValue) {
        return parts.values().stream().filter(p -> p.allowedValues.contains(propValue));
    }

    public static class Builder<T extends Comparable<T>> extends AxialBlockShape<T> {
        private final Class<T> valueClass;

        public Builder(String modelPath, Class<T> valueClass) {
            super(modelPath);
            this.valueClass = valueClass;
        }

        public Builder<T> withCore(float radius) {
            coreRadius = radius;
            //coreTexture = texture;
            double min = 0.5 - radius;
            double max = 0.5 + radius;
            coreShape = Shapes.box(min, min, min, max, max, max);
            return this;
        }

        public Builder<T> withPart(AxialPart<T> part) {
            parts.put(part.name, part);
            part.valueClass = valueClass;
            return this;
        }

        public AxialBlockShape<T> build() {
            return this;
        }
    }
}
