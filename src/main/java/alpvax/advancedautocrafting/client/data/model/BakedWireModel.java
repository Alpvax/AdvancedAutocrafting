package alpvax.advancedautocrafting.client.data.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class BakedWireModel implements IDynamicBakedModel {
  public static final EnumMap<Direction, ModelProperty<String>> DIRECTION_DATA =
      Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction d : Direction.values()) {
          map.put(d, new ModelProperty<>());
        }
      });

  private final ImmutableMap<Direction, ImmutableMultimap<String, IBakedModel>> bakedParts;
  private final boolean isAmbientOcclusion;
  private final boolean isGui3d;
  private final boolean isSideLit;
  private final TextureAtlasSprite particle;
  private final ItemOverrideList overrides;
  private final IModelTransform transforms;
  public BakedWireModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<Direction, ImmutableMultimap<String, IBakedModel>> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides) {
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
    return bakedParts.entrySet().stream().flatMap(e ->
      Optional.ofNullable(extraData.getData(DIRECTION_DATA.get(e.getKey())))
          .map(e.getValue()::get)
          .orElse(ImmutableSet.of())
          .stream()
          .flatMap(model -> model.getQuads(state, side, rand, extraData).stream())
    ).collect(Collectors.toList());
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
