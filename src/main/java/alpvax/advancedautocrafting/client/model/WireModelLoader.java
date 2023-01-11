package alpvax.advancedautocrafting.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WireModelLoader implements IGeometryLoader<WireModelLoader.Geometry> {
    private static final EnumMap<Direction, BlockModelRotation> ROTATIONS = Util.make(new EnumMap<>(Direction.class), m -> {
        m.put(Direction.DOWN, BlockModelRotation.X90_Y0);
        m.put(Direction.UP, BlockModelRotation.X270_Y0);
        m.put(Direction.NORTH, BlockModelRotation.X0_Y0);
        m.put(Direction.SOUTH, BlockModelRotation.X0_Y180);
        m.put(Direction.WEST, BlockModelRotation.X0_Y270);
        m.put(Direction.EAST, BlockModelRotation.X0_Y90);
    });

    @Override
    public Geometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        SubModel core = new SubModel("core", deserialiseCore(deserializationContext, GsonHelper.getAsJsonObject(jsonObject, "core")), null);
        Map<String, SubModel> parts = deserialiseParts(deserializationContext, GsonHelper.getAsJsonObject(jsonObject, "parts"));
        return new Geometry(core, parts);
    }

    private BlockModel deserialiseCore(JsonDeserializationContext context, JsonObject json) {
        if (json.has("radius")) {
            float radius = GsonHelper.getAsFloat(json, "radius");
            if (radius < 0F || radius > 8F) {
                throw new JsonParseException("Core radius must be between 0.0 and 8.0");
            }
            float f = 8F - radius;
            float t = 8F + radius;
            JsonArray from = new JsonArray();
            from.add(f);
            from.add(f);
            from.add(f);
            JsonArray to = new JsonArray();
            to.add(t);
            to.add(t);
            to.add(t);
//            JsonArray elements = new JsonArray();

            json.add("from", from);
            json.add("to", to);
            json.remove("radius");
        }
        return context.deserialize(json, BlockModel.class);
    }

    private Map<String, SubModel> deserialiseParts(JsonDeserializationContext context, JsonObject partsJson) {
        return partsJson.entrySet().stream()
            .map(e -> {
                JsonObject json = GsonHelper.convertToJsonObject(e.getValue(), e.getKey());
                return new SubModel(
                    e.getKey(),
                    GsonHelper.convertToObject(json, "model", context, BlockModel.class),
                    GsonHelper.getAsJsonArray(json, "when", null)
                );
            })
            .collect(Collectors.toMap(m -> m.name, Function.identity()));
    }

    public static class Geometry implements IUnbakedGeometry<Geometry> {
        private final SubModel core;
        private final ImmutableMap<String, SubModel> parts;

        Geometry(SubModel core, Map<String, SubModel> parts) {
            this.core = core;
            this.parts = parts instanceof ImmutableMap ? (ImmutableMap<String, SubModel>) parts : ImmutableMap.copyOf(parts);
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            Material particleLocation = context.getMaterial("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            var rootTransform = context.getRootTransform();
            if (!rootTransform.isIdentity())
                modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

            BakedModel coreModel = core.bakeModel(baker, spriteGetter, modelState, modelLocation);

            ImmutableMap.Builder<Direction, ImmutableMap<BakedModel, Predicate<String>>> bakedParts = ImmutableMap.builder();
            ModelState finalModelState = modelState;
            ROTATIONS.forEach((dir, rotation) -> {
                ImmutableMap.Builder<BakedModel, Predicate<String>> partsForDir = ImmutableMap.builder();
                parts.values().stream().filter(p -> context.isComponentVisible(p.name, true)).forEach(submodel -> {
                    partsForDir.put(submodel.bakeModel(baker, spriteGetter, new Variant(modelLocation, rotation.getRotation(), rotation.isUvLocked() || finalModelState.isUvLocked(), 1), modelLocation), submodel::isValid);
                });
                bakedParts.put(dir, partsForDir.build());
            });
            return new WireBakedModel(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, coreModel, bakedParts.build(), modelState, overrides);
        }

        @Override
        public Set<String> getConfigurableComponentNames()
        {
            return Set.of();
        }
    }

    static class SubModel implements UnbakedModel {
        private final String name;
        private final BlockModel model;
        @Nullable
        private final ImmutableList<String> when;

        SubModel(String name, BlockModel model, @Nullable JsonArray when) {
            this.name = name;
            this.model = model;
            if (when != null) {
                ImmutableList.Builder<String> b = ImmutableList.builder();
                when.forEach(e -> b.add(e.getAsString()));
                this.when = b.build();
            } else {
                this.when = null;
            }
        }

        public boolean isValid(String matchAgainst) {
            return when == null || when.contains(matchAgainst);
        }

        public BakedModel bakeModel(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelstate, ResourceLocation modelLocation)
        {
            return model.bake(baker, model, spriteGetter, modelstate, modelLocation, true);
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            return Collections.emptySet();
        }
        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> p_119538_) {

        }
        @Nullable
        @Override
        public BakedModel bake(
            ModelBaker pBaker, Function<Material, TextureAtlasSprite> pSpriteGetter, ModelState pState,
            ResourceLocation pLocation) {
            return null;
        }
    }
}
