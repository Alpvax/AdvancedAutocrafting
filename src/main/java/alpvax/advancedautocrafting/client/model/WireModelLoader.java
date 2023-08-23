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
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import oshi.util.tuples.Pair;

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
        var corePair = deserialiseCore(deserializationContext, GsonHelper.getAsJsonObject(jsonObject, "core"));
        SubModel core = new SubModel("core", corePair.getA(), null);
        Map<String, SubModel> parts = deserialiseParts(deserializationContext, GsonHelper.getAsJsonObject(jsonObject, "parts"), corePair.getB());
        return new Geometry(core, parts);
    }

    private Pair<BlockModel, Float> deserialiseCore(JsonDeserializationContext context, JsonObject json) {
        float radius = 0;
        if (json.has("radius")) {
            radius = GsonHelper.getAsFloat(json, "radius");
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

            JsonArray elements = GsonHelper.getAsJsonArray(json, "elements", new JsonArray());;
            JsonObject element = new JsonObject();
            element.add("from", from);
            element.add("to", to);
            if (json.has("faces")) {
                element.add("faces", json.remove("faces"));
            }
            elements.add(element);
            json.add("elements", elements);
            json.remove("radius");
        }
        return new Pair<>(context.deserialize(json, BlockModel.class), radius);
    }

    private Map<String, SubModel> deserialiseParts(JsonDeserializationContext context, JsonObject partsJson, float coreRadius) {
        return partsJson.entrySet().stream()
            .map(e -> {
                var json = GsonHelper.convertToJsonObject(e.getValue(), e.getKey());
                var modelJson = GsonHelper.getAsJsonObject(json, "model");
                if (modelJson.has("radius")) {
                    var radius = GsonHelper.getAsFloat(modelJson, "radius");
                    if (radius < 0F || radius > 8F) {
                        throw new JsonParseException("radius must be between 0.0 and 8.0");
                    }
                    var hasFL = modelJson.has("fromLength");
                    var hasTL = modelJson.has("toLength");
                    var hasL = modelJson.has("length");
                    float fromLength;
                    float toLength;
                    if (hasFL && hasTL && hasL) {
                        throw new JsonParseException("fromLength, toLength and length must not all be specified");
                    }
                    if (hasL) {
                        var length = GsonHelper.getAsFloat(modelJson, "length");
                        if (hasTL) {
                            toLength = GsonHelper.getAsFloat(modelJson, "toLength");
                            fromLength = toLength - length;
                        } else {
                            fromLength = GsonHelper.getAsFloat(modelJson, "fromLength", coreRadius);
                            toLength = fromLength + length;
                        }
                    } else if (hasTL) {
                        fromLength = GsonHelper.getAsFloat(modelJson, "fromLength", coreRadius);
                        toLength = GsonHelper.getAsFloat(modelJson, "toLength");
                    } else {
                        throw new JsonParseException("length not fully specified");
                    }
                    var f = 8F - radius;
                    var t = 8F + radius;
                    var from = new JsonArray();
                    from.add(f);
                    from.add(f);
                    from.add(16F - toLength);
                    var to = new JsonArray();
                    to.add(t);
                    to.add(t);
                    to.add(16F - fromLength);

                    var elements = GsonHelper.getAsJsonArray(modelJson, "elements", new JsonArray());
                    var element = new JsonObject();
                    element.add("from", from);
                    element.add("to", to);
                    if (modelJson.has("faces")) {
                        element.add("faces", modelJson.remove("faces"));
                    }
                    elements.add(element);
                    modelJson.add("elements", elements);
                    modelJson.remove("radius");
                    modelJson.remove("toLength");
                    modelJson.remove("fromLength");
                }
                return new SubModel(
                    e.getKey(),
                    context.deserialize(modelJson, BlockModel.class),
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
                modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);

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
