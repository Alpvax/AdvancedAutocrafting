package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.container.util.ContainerBlockHolder;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ControllerScreen extends ContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");

  private float rotation = 45F;
  private final List<BlockGUIRenderer> blocks;

  public ControllerScreen(ControllerContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
    imageWidth = 300;
    imageHeight = 200;
    blocks = container.getBlocks().stream()
                 .map(BlockGUIRenderer::new)
                 .collect(Collectors.toList());
  }

  public static void drawEntityOnScreen(float posX, float posY, float scale, float horzRotation, Entity p_228187_5_) {
    RenderSystem.pushMatrix();
    RenderSystem.translatef(posX, posY, 1050.0F);
    RenderSystem.scalef(1.0F, 1.0F, -1.0F);
    MatrixStack matrixstack = new MatrixStack();
    matrixstack.translate(0.0D, 0.0D, 1000.0D);
    matrixstack.scale(scale, scale, scale);
    Quaternion quaternionz = Vector3f.ZP.rotationDegrees(180.0F);
    Quaternion quaternionx = Vector3f.XP.rotationDegrees(-20F);
    Quaternion quaterniony = Vector3f.YP.rotationDegrees(horzRotation);//Added
    quaternionz.mul(quaternionx);
    quaternionz.mul(quaterniony);//Added
    matrixstack.mulPose(quaternionz);
    EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
    quaternionx.conj();
    entityrenderermanager.overrideCameraOrientation(quaternionx);
    entityrenderermanager.setRenderShadow(false);
    IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
    RenderSystem.runAsFancy(() -> {
      entityrenderermanager.render(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
    });
    irendertypebuffer$impl.endBatch();
    entityrenderermanager.setRenderShadow(true);
    RenderSystem.popMatrix();
  }

  @Override //resize
  protected void init() {
    super.init();
    AtomicInteger j = new AtomicInteger(0);
    //blocks.forEach(b -> b.setPositionAndScale(guiLeft + 8, guiTop + 18 + j.getAndAdd(18), 10));
    // Fit in 32x32 box, with 3px padding between:
    blocks.forEach(b -> b.setPositionAndScale(leftPos + 8, topPos + 18 + j.getAndAdd(35), 30F / 1.7F));
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    blocks.forEach(BlockGUIRenderer::update);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    renderTooltip(matrixStack, mouseX, mouseY);
    rotation = (rotation + partialTicks) % 360F; // 18-sec full rotation
  }

  @Override
  protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
    font.draw(matrixStack, title, (float)titleLabelX, (float)titleLabelY, 4210752);
    font.draw(matrixStack, this.inventory.getDisplayName(), (float)inventoryLabelX, (float)inventoryLabelY, 4210752);

    for (BlockGUIRenderer block : blocks) {
      if (block.renderBlockToolTip(matrixStack, mouseX, mouseY)) {
        break;
      }
    }
  }

  @Override //TODO: actual screen render
  protected void renderBg(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    //noinspection deprecation
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    /*TODO: background
    getMinecraft().getTextureManager().bind(BACKGROUND_TEXTURE);
    int i = getGuiLeft();
    int j = getGuiTop();
    blit(matrixStack, i, j, 0, 0, this.imageWidth, 3 * 18 + 17);
    blit(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.imageWidth, 96);
     */
    for (BlockGUIRenderer block : blocks) {
      //block.renderBlock(rotation);
      block.renderBlock(matrixStack, rotation);
    }
  }

  private FallingBlockEntity makeEntity(BlockState state) {
    Vector3d pos = getMinecraft().player.position();
    FallingBlockEntity entity = new FallingBlockEntity(getMinecraft().level, pos.x, pos.y, pos.z, state);
    entity.fallDistance = Integer.MIN_VALUE;
    return entity;
  }


  public class BlockGUIRenderer {
    private final ContainerBlockHolder holder;
    //private BlockState state = null;
    private FallingBlockEntity entity;
    private ITextComponent name;
    private float scale = 10F;
    private int x;
    private int y;
    public BlockGUIRenderer(ContainerBlockHolder holder) {
      this.holder = holder;
      holder.addListener(h -> this.update());
    }

    public void setPositionAndScale(int x, int y, float scale) {
      this.x = x;
      this.y = y;
      this.scale = scale;
    }

    private void update() {
      BlockState s = holder.getBlockState();
      entity = makeEntity(s);
      ItemStack stack = new ItemStack(s.getBlock());
      name = stack.getDisplayName();
    }

    public boolean isHovered(int mouseX, int mouseY) {
      return mouseX >= x && mouseX - x <  scale * 1.414F && mouseY >= y && mouseY - y < scale * 1.7F;
    }

    public void renderBlock(int x, int y, int scale, float rotation) {
      setPositionAndScale(x, y, scale);
      renderBlock(rotation);
    }
    public void renderBlock(float rotation) {
      drawEntityOnScreen(x + 1F + scale * 1.414F / 2F, y + scale * 1.054F, scale, rotation, entity);
    }
    public void renderBlock1(MatrixStack stack, float rotation) {
      Quaternion q = Vector3f.XP.rotationDegrees(-20F);
      q.mul(Vector3f.YP.rotationDegrees(rotation));
      stack.pushPose();
      //stack.translate(-3, -(y - guiTop -18 + 2) / 35F, 0);
      stack.translate(0, 0, -2);
      stack.scale(-0.125F, 0.125F, -0.125F);
      //stack.translate(0, -(y - guiTop -18) / 35F, 0);
      stack.translate(-3, -(y - topPos -18 + 2) / 35F, 0);
      /*stack.translate(0.5, 0, 0.5);
      stack.rotate(Vector3f.YP.rotationDegrees(rotation));
      stack.translate(-0.5, 0, -0.5);*/
      stack.pushPose();
      stack.translate(0.5, 0, 0.5);
      stack.mulPose(q);
      stack.translate(-0.5, 0, -0.5);
      Minecraft mc = ControllerScreen.this.getMinecraft();
      mc.getBlockRenderer().renderBlock(
          holder.getBlockState(),
          stack,
          mc.renderBuffers().bufferSource(),
          /*15728880,//*/
          LightTexture.pack(15, 15),//*/
          OverlayTexture.NO_OVERLAY,
          EmptyModelData.INSTANCE
      );
      stack.popPose();
      stack.popPose();
    }
    public void renderBlock(MatrixStack stack, float rotation) {
      Quaternion q = Vector3f.XP.rotationDegrees(-20F);
      q.mul(Vector3f.YP.rotationDegrees(rotation));
      BlockState state = holder.getBlockState();
      Minecraft mc = ControllerScreen.this.getMinecraft();
      BlockRendererDispatcher dispatcher = mc.getBlockRenderer();
      IRenderTypeBuffer buffer = mc.renderBuffers().bufferSource();
      int combinedLight = 15728880;//LightTexture.packLight(15, 15);
      stack.pushPose();
      stack.translate(0, 0, -2);
      stack.scale(-0.125F, 0.125F, -0.125F);
      stack.translate(0.5, 0, 0.5);
      stack.mulPose(q);
      BlockRenderType blockrendertype = state.getRenderShape();
      switch(blockrendertype) {
        case MODEL:
          IBakedModel ibakedmodel = dispatcher.getBlockModel(state);
          int i = mc.getBlockColors().getColor(state, null, null, 0);
          float f = (float)(i >> 16 & 255) / 255.0F;
          float f1 = (float)(i >> 8 & 255) / 255.0F;
          float f2 = (float)(i & 255) / 255.0F;
          for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
            if (RenderTypeLookup.canRenderInLayer(state, type)) {
              net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
              dispatcher.getModelRenderer().renderModel(
                  stack.last(),
                  //buffer.getBuffer(RenderTypeLookup.func_239220_a_(state, false)),
                  buffer.getBuffer(type),
                  state,
                  ibakedmodel,
                  f, f1, f2,
                  combinedLight,
                  OverlayTexture.NO_OVERLAY,
                  EmptyModelData.INSTANCE
              );
            }
          }
          net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
          break;
        case ENTITYBLOCK_ANIMATED:
          ItemStack item = new ItemStack(state.getBlock());
          item.getItem().getItemStackTileEntityRenderer().renderByItem(
              item,
              ItemCameraTransforms.TransformType.NONE,
              stack,
              buffer,
              combinedLight,
              OverlayTexture.NO_OVERLAY
          );
          break;
      }
      stack.popPose();
    }

    public boolean renderBlockToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
      if (isHovered(mouseX, mouseY)) {
        Minecraft mc = getMinecraft();
        BlockPos pos = holder.getPos();
        List<ITextComponent> tooltip = Lists.newArrayList(
            name,
            new StringTextComponent(String.format("Position: {%d, %d, %d}", pos.getX(), pos.getY(), pos.getZ())).withStyle(TextFormatting.GRAY)
        );
        if (hasShiftDown() || !holder.isClientWorld()) {
          tooltip.add(new StringTextComponent("Dimension: " + holder.getWorldID().toString()).withStyle(TextFormatting.GRAY));
        }
        renderToolTip(matrixStack, Lists.transform(tooltip, ITextComponent::getVisualOrderText), mouseX - leftPos, mouseY - topPos, font);
        return true;
      }
      return false;
    }
  }
}
