package alpvax.advancedautocrafting.client.data.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WireModelBuilder<T extends Enum<T> & IStringSerializable> {
  static final EnumMap<Direction, ModelRotation> ROTATIONS = Util.make(new EnumMap<>(Direction.class), m -> {
    m.put(Direction.DOWN, ModelRotation.X90_Y0);
    m.put(Direction.UP, ModelRotation.X270_Y0);
    m.put(Direction.NORTH, ModelRotation.X0_Y0);
    m.put(Direction.SOUTH, ModelRotation.X0_Y180);
    m.put(Direction.WEST, ModelRotation.X0_Y270);
    m.put(Direction.EAST, ModelRotation.X0_Y90);
  });

  public static Map<String, Either<RenderMaterial, String>> parseTextures(JsonObject json) {
    return JSONUtils.getJsonObject(json, "textures", new JsonObject())
        .entrySet().stream().map(e -> {
      Either<RenderMaterial, String> either;
      String name = e.getValue().getAsString();
      if (name.charAt(0) == '#') {
        either = Either.right(name.substring(1));
      } else {
        ResourceLocation resourcelocation = ResourceLocation.tryCreate(name);
        if (resourcelocation == null) {
          throw new JsonParseException(name + " is not valid resource location");
        }
        either = Either.left(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, resourcelocation));
      }
      return Pair.of(e.getKey(), either);
    })
    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
  }

  private final Class<T> type;
  private Map<String, Either<RenderMaterial, String>> textures;
  private Multimap<String, SubModel> parts = HashMultimap.create();
  public WireModelBuilder(Class<T> connectionType) {
    type = connectionType;
  }

  WireModelBuilder<T> loadTextures(JsonObject json) {
    return withTextures(parseTextures(json));
  }
  public WireModelBuilder<T> withTextures(Map<String, Either<RenderMaterial, String>> toplevelTextures) {
    textures = toplevelTextures;
    return this;
  }

  public WireModelBuilder<T> withCore(BlockModel core) {
    put(new SubModel("core", core), Arrays.asList(type.getEnumConstants()));
    return this;
  }

  public WireModelBuilder<T> withPart(String name, BlockModel model, Iterable<T> valid) {
    put(new SubModel(name, model), valid);
    return this;
  }

  protected void put(SubModel model, Iterable<T> valid) {
    for (T v : valid) {
      parts.put(v.getString(), model);
    }
  }

  public Geometry build() {
    textures.forEach((s, e) -> {
      parts.values().forEach(p -> p.model.textures.putIfAbsent(s, e));
    });
    return new Geometry(ImmutableMultimap.copyOf(parts));//TODO:new WireModelLoader.Geometry(new WireModelLoader.SubModel("core", , null), new HashMap<>());//TODO
  }

  class Geometry implements IMultipartModelGeometry<Geometry> {
    private final ImmutableMultimap<String, SubModel> parts;
    private final ImmutableMap<String, SubModel> byName;
    public Geometry(ImmutableMultimap<String, SubModel> parts) {
      this.parts = parts;
      //noinspection UnstableApiUsage
      byName = parts.values().stream().distinct().collect(ImmutableMap.toImmutableMap(SubModel::name, Function.identity()));
    }

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
      return parts.values();
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
      return Optional.ofNullable(byName.get(name));
    }

    public Collection<? extends IModelGeometryPart> getParts(T filter) {
      return parts.get(filter.getString());
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      RenderMaterial particleLocation = owner.resolveTexture("particle");
      TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

      ImmutableMap.Builder<Direction, ImmutableMultimap<String, IBakedModel>> bakedParts = ImmutableMap.builder();
      ROTATIONS.forEach((dir, rotation) -> {
        ImmutableMultimap.Builder<String, IBakedModel> partsForDir = ImmutableMultimap.builder();
        parts.entries().stream()
            .filter(e -> owner.getPartVisibility(e.getValue()))
            .forEach(e -> partsForDir.put(e.getKey(), e.getValue().bakeModel(
                bakery,
                spriteGetter,
                new ModelTransformComposition(
                    rotation,
                    modelTransform,
                    rotation.isUvLock() || modelTransform.isUvLock()
                ),
                modelLocation
            )));
        bakedParts.put(dir, partsForDir.build());
      });
      return new BakedWireModel(owner.isShadedInGui(), owner.useSmoothLighting(), owner.isSideLit(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
    }
  }

  private static class SubModel implements IModelGeometryPart {
    private final String name;
    private final BlockModel model;

    SubModel(String name, BlockModel model) {
      this.name = name;
      this.model = model;
      this.model.name = name;
    }

    @Override
    public String name() {
      return name;
    }
    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
    {
      throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
    }

    public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
    {
      return model.bakeModel(bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
      return model.getTextures(modelGetter, missingTextureErrors);
    }
  }
}
