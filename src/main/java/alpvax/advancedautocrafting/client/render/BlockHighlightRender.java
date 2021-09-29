package alpvax.advancedautocrafting.client.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Credit to DireWolf20/MiningGadgets for the original class (BlockOverlayRender)
 * Heavily modified (re-written) by Alpvax, with caching
 */
public class BlockHighlightRender {
  private static final Direction[] DIRECTIONS = Direction.values();
  public static final ContigiousRegionManager manager = new ContigiousRegionManager();

  private static class HighlightData {
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;
    /** Whether or not the render can be seen through walls (no depth test) */
    private boolean throughWalls;
    /** Which directions to render faces in */
    private final boolean[] dirs = new boolean[6];
    /** [x_min, x_max] */
    private double[] xVert = {-0.5, 0.5};
    /** [y_min, y_max] */
    private double[] yVert = {-0.5, 0.5};
    /** [z_min, z_max] */
    private double[] zVert = {-0.5, 0.5};
    protected HighlightData(int red, int green, int blue, int alpha, boolean throughWalls) {
      this.red = red;
      this.blue = blue;
      this.green = green;
      this.alpha = alpha;
      this.throughWalls = throughWalls;
    }
    protected HighlightData setDirection(Direction d, boolean show) {
      if(dirs[d.get3DDataValue()] != show) { //Only adjust if direction is different
        dirs[d.get3DDataValue()] = show;
        int index = d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0;
        double amount = (index == 0) == show ? -0.005 : 0.005;
        switch (d.getAxis()) {
          case X -> xVert[index] += amount;
          case Y -> yVert[index] += amount;
          case Z -> zVert[index] += amount;
        }
      }
      return this;
    }
    protected void addVertexData(VertexConsumer builder) {
      if(dirs[Direction.DOWN.get3DDataValue()]) {
        builder.vertex(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
      }
      if(dirs[Direction.UP.get3DDataValue()]) {
        builder.vertex(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.NORTH.get3DDataValue()]) {
        builder.vertex(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.SOUTH.get3DDataValue()]) {
        builder.vertex(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.WEST.get3DDataValue()]) {
        builder.vertex(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.EAST.get3DDataValue()]) {
        builder.vertex(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        builder.vertex(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
      }
    }
  }
  public static class ContigiousRegionManager {
    private final Map<BlockPos, HighlightData> cache = Maps.newHashMap();
    public void add(BlockPos pos, int red, int green, int blue, int alpha) {
      add(pos, new HighlightData(red, green, blue, alpha, false));
    }
    private HighlightData add(BlockPos pos, HighlightData data) {
      cache.put(pos, data);
      for(Direction d : DIRECTIONS) {
        BlockPos aPos = pos.relative(d);
        HighlightData aData = cache.get(aPos);
        data.setDirection(d, aData == null);
        if(aData != null) {
          aData.setDirection(d.getOpposite(), false);
        }
      }
      return data;
    }
    public boolean remove(BlockPos pos) {
      if(cache.remove(pos) != null) {
        for(Direction d : DIRECTIONS) {
          BlockPos aPos = pos.relative(d);
          HighlightData aData = cache.get(aPos);
          if(aData != null) {
            aData.setDirection(d.getOpposite(), true);
          }
        }
        return true;
      }
      return false;
    }
    @Nullable
    protected HighlightData get(BlockPos pos) {
      return cache.get(pos);
    }
    protected HighlightData getOrCreate(BlockPos pos, int red, int green, int blue, int alpha) {
      HighlightData data = get(pos);
      if (data == null) {
        data = add(pos, new HighlightData(red, green, blue, alpha, false));
      }
      return data;
    }

    public boolean contains(BlockPos pos) {
      return cache.containsKey(pos);
    }

    public void clear() {
      cache.clear();
    }
  }

  public static void render(PoseStack poseStack) {
    Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

    poseStack.pushPose();
    poseStack.translate(-camPos.x(), -camPos.y(), -camPos.z()); // translate back to camera

    VertexConsumer builder = buffer.getBuffer(AARenderTypes.BLOCK_OVERLAY);
    //TODO: no depth? VertexConsumer throughWalls = buffer.getBuffer(AARenderTypes.BLOCK_OVERLAY_NO_DEPTH);
    /*Matrix4f matrix4f = poseStack.last().pose(); // get final transformation matrix, handy to get yaw+pitch transformation
    RenderSystem.multMatrix(matrix4f);
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();

    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuilder();*/

    manager.cache.forEach((pos, data) -> {
      if(pos != null) {
        poseStack.pushPose();
        //RenderSystem.translatef(e.getX() - 0.005F, e.getY() - 0.005F, e.getZ() - 0.005F);
        poseStack.translate(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        data.addVertexData(builder);
        poseStack.popPose();
      }
    });
    poseStack.popPose();

    buffer.endBatch(AARenderTypes.BLOCK_OVERLAY); //TODO: Own rendertype?

    //RenderSystem.disableBlend();
    //RenderSystem.enableTexture();
  }
}
