package alpvax.advancedautocrafting.client.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Credit to DireWolf20/MiningGadgets for the original class (BlockOverlayRender)
 * Heavily modified (re-written) by Alpvax, with caching
 */
public class BlockHighlightRender {
  private static final Direction[] DIRECTIONS = Direction.values();
  private static final ContigiousRegionManager manager = new ContigiousRegionManager();

  private static class HighlightData {
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;
    private boolean[] dirs = new boolean[6];
    //** [[x_min, x_max], [y_min, y_max], [z_min, z_max]] */
    private double[] xVert = {0, 1.01};
    private double[] yVert = {0, 1.01};
    private double[] zVert = {0, 1.01};
    protected HighlightData(int red, int green, int blue, int alpha) {
      this.red = red;
      this.blue = blue;
      this.green = green;
      this.alpha = alpha;
    }
    protected HighlightData setDirection(Direction d, boolean show) {
      dirs[d.getIndex()] = show;
      int index = d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0;
      double amount = (index == 0) == show ? -0.005 : 0.005;
      switch (d.getAxis()) {
        case X:
          xVert[index] += amount;
          break;
        case Y:
          yVert[index] += amount;
          break;
        case Z:
          zVert[index] += amount;
          break;
      }
      return this;
    }
    protected void addVertexData(BufferBuilder buffer) {
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
      if(dirs[Direction.DOWN.getIndex()]) {
        buffer.pos(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
      } else { //TODO: Debugging. remove
        buffer.pos(xVert[0], yVert[0], zVert[0]).color(0, 0, 255, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[0]).color(0, 0, 255, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[1]).color(0, 0, 255, alpha).endVertex();
        buffer.pos(xVert[0], yVert[0], zVert[1]).color(0, 0, 255, alpha).endVertex();
      }
      if(dirs[Direction.UP.getIndex()]) {
        buffer.pos(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
      } else { //TODO: Debugging. remove
        buffer.pos(xVert[0], yVert[1], zVert[0]).color(255, 0, 0, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[1]).color(255, 0, 0, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[1]).color(255, 0, 0, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[0]).color(255, 0, 0, alpha).endVertex();
      }
      if (dirs[Direction.NORTH.getIndex()]) {
        buffer.pos(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.SOUTH.getIndex()]) {
        buffer.pos(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.WEST.getIndex()]) {
        buffer.pos(xVert[0], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[1]).color(red, green, blue, alpha).endVertex();
      }
      if (dirs[Direction.EAST.getIndex()]) {
        buffer.pos(xVert[0], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[0], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[1], zVert[0]).color(red, green, blue, alpha).endVertex();
        buffer.pos(xVert[1], yVert[0], zVert[0]).color(red, green, blue, alpha).endVertex();
      }
    }
  }
  private static class ContigiousRegionManager {
    private Map<BlockPos, HighlightData> cache = Maps.newHashMap();
    protected void add(BlockPos pos, int red, int green, int blue, int alpha) {
      add(pos, new HighlightData(red, green, blue, alpha));
    }
    protected HighlightData add(BlockPos pos, HighlightData data) {
      cache.put(pos, data);
      for(Direction d : DIRECTIONS) {
        BlockPos aPos = pos.offset(d);
        HighlightData aData = cache.get(aPos);
        data.setDirection(d, aData == null);
        if(aData != null) {
          aData.setDirection(d.getOpposite(), false);
        }
      }
      return data;
    }
    protected boolean remove(BlockPos pos) {
      if(cache.remove(pos) != null) {
        for(Direction d : DIRECTIONS) {
          BlockPos aPos = pos.offset(d);
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
        data = add(pos, new HighlightData(red, green, blue, alpha));
      }
      return data;
    }
  }

  private int alphaToInt(float alpha) {
    return MathHelper.clamp(Math.round(alpha * 256), 0, 256);
  }

  public static void render(Iterable<BlockPos> coords, MatrixStack matrixStack) {
    ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();

    RenderSystem.pushMatrix();
    matrixStack.push();
    matrixStack.translate(-renderInfo.getProjectedView().getX(), -renderInfo.getProjectedView().getY(), -renderInfo.getProjectedView().getZ()); // translate back to camera
    Matrix4f matrix4f = matrixStack.getLast().getPositionMatrix(); // get final transformation matrix, handy to get yaw+pitch transformation
    RenderSystem.multMatrix(matrix4f);
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();

    //RenderSystem.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    //TODO: Remove positions correctly to make cache actually work
    manager.cache.clear();
    coords.forEach(e -> {
      if(e != null) {
        HighlightData data = manager.getOrCreate(e, 69, 120, 18, 160);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(e.getX() - 0.005F, e.getY() - 0.005F, e.getZ() - 0.005F);
        data.addVertexData(buffer);
        tessellator.draw();
        //RenderSystem.scalef(1.01f, 1.01f, 1.01f);
        //BlockHighlightRender.render(e, tessellator, buffer, 69, 120, 18, 160);
        RenderSystem.popMatrix();
      }
    });
    matrixStack.pop();

    RenderSystem.disableBlend();
    RenderSystem.enableTexture();
    RenderSystem.popMatrix();
  }
}
