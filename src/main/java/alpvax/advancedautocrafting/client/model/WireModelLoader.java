package alpvax.advancedautocrafting.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.CompositeModelState;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WireModelLoader implements IModelLoader<WireModelLoader.Geometry> {
    private static final EnumMap<Direction, BlockModelRotation> ROTATIONS = Util.make(new EnumMap<>(Direction.class), m -> {
        m.put(Direction.DOWN, BlockModelRotation.X90_Y0);
        m.put(Direction.UP, BlockModelRotation.X270_Y0);
        m.put(Direction.NORTH, BlockModelRotation.X0_Y0);
        m.put(Direction.SOUTH, BlockModelRotation.X0_Y180);
        m.put(Direction.WEST, BlockModelRotation.X0_Y270);
        m.put(Direction.EAST, BlockModelRotation.X0_Y90);
    });

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }

    @Override
    public Geometry read(
        JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        SubModel core = new SubModel("core", deserialiseCore(deserializationContext, GsonHelper.getAsJsonObject(modelContents, "core")), null);
        Map<String, SubModel> parts = deserialiseParts(deserializationContext, GsonHelper.getAsJsonObject(modelContents, "parts"));
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
            JsonArray elements = new JsonArray();

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
            .collect(Collectors.toMap(SubModel::name, Function.identity()));
    }

    public static class Geometry implements IMultipartModelGeometry<Geometry> {
        private final SubModel core;
        private final ImmutableMap<String, SubModel> parts;

        Geometry(SubModel core, Map<String, SubModel> parts) {
            this.core = core;
            this.parts = parts instanceof ImmutableMap ? (ImmutableMap<String, SubModel>) parts : ImmutableMap.copyOf(parts);
        }

        @Override
        public Collection<? extends IModelGeometryPart> getParts() {
            return parts.values();//TODO: add core?
        }

        @Override
        public Optional<? extends IModelGeometryPart> getPart(String name) {
            return name.equals("core") ? Optional.of(core) : Optional.ofNullable(parts.get(name));
        }

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelstate, ItemOverrides overrides, ResourceLocation modelLocation) {
            Material particleLocation = owner.resolveTexture("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            BakedModel coreModel = core.bakeModel(bakery, spriteGetter, modelstate, modelLocation);

            ImmutableMap.Builder<Direction, ImmutableMap<BakedModel, Predicate<String>>> bakedParts = ImmutableMap.builder();
            ROTATIONS.forEach((dir, rotation) -> {
                ImmutableMap.Builder<BakedModel, Predicate<String>> partsForDir = ImmutableMap.builder();
                parts.values().stream().filter(owner::getPartVisibility).forEach(submodel -> {
                    partsForDir.put(submodel.bakeModel(bakery, spriteGetter, new CompositeModelState(rotation, modelstate,
                                                                                                           rotation.isUvLocked() || modelstate.isUvLocked()), modelLocation), submodel::isValid);
                });
                bakedParts.put(dir, partsForDir.build());
            });
            return new WireBakedModel(owner.isShadedInGui(), owner.useSmoothLighting(), owner.isSideLit(), particle, coreModel, bakedParts.build(), owner.getCombinedTransform(), overrides);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            Set<Material> textures = new HashSet<>(core.getTextures(owner, modelGetter, missingTextureErrors));
            for (SubModel part : parts.values()) {
                textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
            }
            return textures;
        }
    }

    static class SubModel implements IModelGeometryPart {
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

        @Override
        public String name() {
            return name;
        }
        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelstate, ResourceLocation modelLocation)
        {
            throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
        }

        public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelstate, ResourceLocation modelLocation)
        {
            return model.bake(bakery, model, spriteGetter, modelstate, modelLocation, true);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            return model.getMaterials(modelGetter, missingTextureErrors);
        }
    }
}
