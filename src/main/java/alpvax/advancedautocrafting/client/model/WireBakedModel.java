package alpvax.advancedautocrafting.client.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class WireBakedModel implements IDynamicBakedModel {
    public static final EnumMap<Direction, ModelProperty<String>> DIRECTION_DATA =
        Util.make(new EnumMap<>(Direction.class), map -> {
            for (Direction d : Direction.values()) {
                map.put(d, new ModelProperty<>());
            }
        });

    private final BakedModel coreModel;
    private final ImmutableMap<Direction, ImmutableMap<BakedModel, Predicate<String>>> bakedParts;
    private final boolean isAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final ModelState transforms;

    public WireBakedModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, BakedModel coreModel, ImmutableMap<Direction, ImmutableMap<BakedModel, Predicate<String>>> bakedParts, ModelState combinedTransform, ItemOverrides overrides) {
        this.coreModel = coreModel;
        this.bakedParts = bakedParts;
        this.isAmbientOcclusion = isAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.isSideLit = isSideLit;
        this.particle = particle;
        this.overrides = overrides;
        transforms = combinedTransform;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData) {
        List<BakedQuad> quads = new ArrayList<>(coreModel.getQuads(state, side, rand, modelData));
        quads.addAll(coreModel.getQuads(state, side, rand, modelData));
        bakedParts.forEach((dir, parts) -> Optional.ofNullable(modelData.getData(DIRECTION_DATA.get(dir)))
            .ifPresent(valid -> parts.forEach((model, predicate) -> {
                if (predicate.test(valid)) {
                    quads.addAll(model.getQuads(state, side, rand, modelData));
                }
            })));
        return quads;
    }

//TODO: get data from blockstate/tileentity
//    @Nonnull
//    @Override
//    public IModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, IModelData modelData)
//    {
//        WireModelData data = new WireModelData();
//        for(Map.Entry<String, IBakedModel> entry : bakedParts.entrySet())
//        {
//            //data.putSubmodelData(entry.getKey(), entry.getValue().getModelData(world, pos, state, CompositeModel.ModelDataWrapper.wrap(tileData)));
//        }
//        return data;
//    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return isAmbientOcclusion;
    }

    @Override
    public boolean isGui3d()
    {
        return isGui3d;
    }

    @Override
    public boolean usesBlockLight()
    {
        return isSideLit;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return particle;
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides()
    {
        return overrides;
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return true;
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
    {
        return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, poseStack);
    }
}
