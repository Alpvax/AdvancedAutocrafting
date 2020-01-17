package alpvax.advancedautocrafting.client.render;

/**
 * Credit to DireWolf20/MiningGadgets for the original class (BlockOverlayRender)
 */
public class BlockHighlightRender {

  /*TODO: Create highlight
  public static void render(ControllerTileEntity tile) {
    final Minecraft mc = Minecraft.getInstance();

    int range = MiningProperties.getBeamRange(item);
    BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(mc.player, RayTraceContext.FluidMode.NONE, range);
    if (mc.world.getBlockState(VectorHelper.getLookingAt(mc.player, item, range).getPos()) == Blocks.AIR.getDefaultState()) {
      return;
    }

    List<BlockPos> coords = MiningCollect.collect(mc.player, lookingAt, mc.world, MiningProperties.getRange(item));

    Vec3d playerPos = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);

    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture();

    GlStateManager.translated(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    coords.forEach(e -> {
      if (mc.world.getBlockState(e).getBlock() != ModBlocks.RENDER_BLOCK.get()) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(e.getX(), e.getY(), e.getZ());
        GlStateManager.translatef(-0.005f, -0.005f, -0.005f);
        GlStateManager.scalef(1.01f, 1.01f, 1.01f);
        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
//              Removed as we'd have to build the tool and check all the upgrades etc on a single tick. Todo: cache most of it?
//                if ( UpgradeTools.containsActiveUpgrade(item, Upgrade.VOID_JUNK) ) {
//
//                    BlockOverlayRender.render(e, tessellator, buffer, Color.RED);
//                }
//                else
        BlockHighlightRender.render(e, tessellator, buffer, Color.GREEN);
        GlStateManager.popMatrix();
      }
    });

    GlStateManager.disableBlend();
    GlStateManager.enableTexture();
    GlStateManager.popMatrix();
  }*/

  /*TODO: Update mappings / establish correct methods
  public static void render(BlockPos pos, Tessellator tessellator, BufferBuilder buffer, Color color) {
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

    double maxX = pos.getX() + 1, maxY = pos.getY() + 1, maxZ = pos.getZ() + 1;
    float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f, alpha = .125f;

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
  }*/
}
