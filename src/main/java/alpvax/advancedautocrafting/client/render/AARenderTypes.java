package alpvax.advancedautocrafting.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class AARenderTypes extends RenderType { // Extends RnederType just so that it can access protected members
    public static final RenderType BLOCK_OVERLAY = create(
        "MiningLaserBlockOverlay",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
        256, false, false,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setTextureState(NO_TEXTURE)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
    );

  /*TODO: through walls?
  public static final RenderType BLOCK_OVERLAY_NO_DEPTH = create("MiningLaserBlockOverlay",
      DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,false, false,
      RenderType.CompositeState.builder()
          .setShaderState(ShaderStateShard.POSITION_COLOR_SHADER)
          .setLayeringState(VIEW_OFFSET_Z_LAYERING)
          .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
          .setTextureState(NO_TEXTURE)
          .setDepthTestState(NO_DEPTH_TEST)
          .setCullState(CULL)
          .setLightmapState(NO_LIGHTMAP)
          .setWriteMaskState(COLOR_DEPTH_WRITE)
          .createCompositeState(false));
   */

    @SuppressWarnings("unused") //Never instantiated, but required to extend RenderType
    public AARenderTypes(
        String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_,
        boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }
}
