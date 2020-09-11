package alpvax.advancedautocrafting.client.data.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
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

  private final IBakedModel coreModel;
  private final ImmutableMap<Direction, ImmutableMap<IBakedModel, Predicate<String>>> bakedParts;
  private final boolean isAmbientOcclusion;
  private final boolean isGui3d;
  private final boolean isSideLit;
  private final TextureAtlasSprite particle;
  private final ItemOverrideList overrides;
  private final IModelTransform transforms;

  public WireBakedModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, IBakedModel coreModel, ImmutableMap<Direction, ImmutableMap<IBakedModel, Predicate<String>>> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides) {
    this.coreModel = coreModel;
    this.bakedParts = bakedParts;
    this.isAmbientOcclusion = isAmbientOcclusion;
    this.isGui3d = isGui3d;
    this.isSideLit = isSideLit;
    this.particle = particle;
    this.overrides = overrides;
    this.transforms = combinedTransform;
  }

  @Nonnull
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
    List<BakedQuad> quads = new ArrayList<>(coreModel.getQuads(state, side, rand, extraData));
    quads.addAll(coreModel.getQuads(state, side, rand, extraData));
    bakedParts.forEach((dir, parts) -> {
      Optional.ofNullable(extraData.getData(DIRECTION_DATA.get(dir))).ifPresent(valid -> {
        parts.forEach((model, predicate) -> {
          //if (valid.stream().anyMatch(predicate)) {
          if (predicate.test(valid)) {
            quads.addAll(model.getQuads(state, side, rand, extraData));
          }
        });
      });
    });
    return quads;
  }

  /*@Nonnull
  @Override
  public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
  {
    WireModelData data = new WireModelData();
    for(Map.Entry<String, IBakedModel> entry : bakedParts.entrySet())
    {
      //data.putSubmodelData(entry.getKey(), entry.getValue().getModelData(world, pos, state, CompositeModel.ModelDataWrapper.wrap(tileData)));
    }
    return data;
  }*/

  @Override
  public boolean isAmbientOcclusion()
  {
    return isAmbientOcclusion;
  }

  @Override
  public boolean isGui3d()
  {
    return isGui3d;
  }

  @Override
  public boolean func_230044_c_()
  {
    return isSideLit;
  }

  @Override
  public boolean isBuiltInRenderer()
  {
    return false;
  }

  @Nonnull
  @Override
  public TextureAtlasSprite getParticleTexture()
  {
    return particle;
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides()
  {
    return overrides;
  }

  @Override
  public boolean doesHandlePerspectives()
  {
    return true;
  }

  @Override
  public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
  {
    return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, mat);
  }
}
