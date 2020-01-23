package alpvax.advancedautocrafting.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 * Credit to DireWolf20/MiningGadgets for the original class (BlockOverlayRender)
 */
public class BlockHighlightRender {

  /*TODO: Create highlight*/
  public static void render(Iterable<BlockPos> coords, MatrixStack matrixStack) {
    final Minecraft mc = Minecraft.getInstance();

    //Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
    //Vec3d playerPos = mc.player.getPositionVector();

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

    coords.forEach(e -> {
      if(e != null) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(e.getX() - 0.005F, e.getY() - 0.005F, e.getZ() + 1.005F);
        RenderSystem.scalef(1.01f, 1.01f, 1.01f);
        BlockHighlightRender.render(e, tessellator, buffer, 69, 120, 18, 160);
        RenderSystem.popMatrix();
      }
    });
    matrixStack.pop();

    RenderSystem.disableBlend();
    RenderSystem.enableTexture();
    RenderSystem.popMatrix();
  }

  public static void render(BlockPos pos, Tessellator tessellator, BufferBuilder buffer, int red, int green, int blue, int alpha) {
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

    double maxX = pos.getX() + 1, maxY = pos.getY() + 1, maxZ = pos.getZ() + 1;

    double startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;

    buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();

    //up
    buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();

    //east
    buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();

    //west
    buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();

    //south
    buffer.pos(endX, startY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, endY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(endX, startY, endZ).color(red, green, blue, alpha).endVertex();

    //north
    buffer.pos(startX, startY, startZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, startY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, endY, endZ).color(red, green, blue, alpha).endVertex();
    buffer.pos(startX, endY, startZ).color(red, green, blue, alpha).endVertex();
    tessellator.draw();
  }
}
