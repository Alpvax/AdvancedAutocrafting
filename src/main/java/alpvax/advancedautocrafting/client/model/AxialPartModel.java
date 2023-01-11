package alpvax.advancedautocrafting.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class AxialPartModel implements IDynamicBakedModel {
    private final ImmutableMap<String, BakedModel> bakedParts;
    private final boolean useAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final ModelState transforms;
    public AxialPartModel(
        ImmutableMap<String, BakedModel> parts, boolean useAmbientOcclusion, boolean isGui3d, boolean useBlockLight,
        TextureAtlasSprite particle, ItemOverrides overrides,
        ModelState transforms) {
        bakedParts = parts;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.particle = particle;
        this.overrides = overrides;
        this.transforms = transforms;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(
        @Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return null;
    }
    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }
    @Override
    public boolean isGui3d() {
        return isGui3d;
    }
    @Override
    public boolean usesBlockLight() {
        return useBlockLight;
    }
    @Override
    public boolean isCustomRenderer() {
        return false;
    }
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }
    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    public static class Loader implements IModelLoader<AxialPartModel.Geometry> {
        @Override
        public Geometry read(
            JsonDeserializationContext deserializationContext, JsonObject modelContents) {

            return null;
        }
        @Override
        public void onResourceManagerReload(ResourceManager pResourceManager) {

        }
    }

    public static class Geometry implements IModelGeometry<Geometry> {
        @Override
        public BakedModel bake(
            IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return null;
        }
        @Override
        public Collection<Material> getTextures(
            IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors) {
            return null;
        }
    }
}
