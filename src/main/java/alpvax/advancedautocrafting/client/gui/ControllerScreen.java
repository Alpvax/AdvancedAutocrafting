package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FourWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

public class ControllerScreen extends ContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  private float rotation = 45F;

  public ControllerScreen(ControllerContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
  }

  public static void drawEntityOnScreen(int posX, int posY, int scale, float horzRotation, Entity p_228187_5_) {
    RenderSystem.pushMatrix();
    RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
    RenderSystem.scalef(1.0F, 1.0F, -1.0F);
    MatrixStack matrixstack = new MatrixStack();
    matrixstack.translate(0.0D, 0.0D, 1000.0D);
    matrixstack.scale((float)scale, (float)scale, (float)scale);
    Quaternion quaternionz = Vector3f.ZP.rotationDegrees(180.0F);
    Quaternion quaternionx = Vector3f.XP.rotationDegrees(-20F);
    Quaternion quaterniony = Vector3f.YP.rotationDegrees(horzRotation);//Added
    quaternionz.multiply(quaternionx);
    quaternionz.multiply(quaterniony);//Added
    matrixstack.rotate(quaternionz);
    EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
    quaternionx.conjugate();
    entityrenderermanager.setCameraOrientation(quaternionx);
    entityrenderermanager.setRenderShadow(false);
    IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
    RenderSystem.runAsFancy(() -> {
      entityrenderermanager.renderEntityStatic(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
    });
    irendertypebuffer$impl.finish();
    entityrenderermanager.setRenderShadow(true);
    RenderSystem.popMatrix();
  }

  @Override //render
  public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    func_230446_a_(matrixStack);//renderBackground
    super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    func_230459_a_(matrixStack, mouseX, mouseY);//renderHoveredToolTip
    rotation = (rotation + 1) % 360F; // 18-sec full rotation
  }
  /*@Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }*/

  @Override //drawGuiContainerForegroundLayer
  protected void func_230451_b_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
    field_230712_o_.func_238422_b_(matrixStack, field_230704_d_, (float)this.field_238742_p_, (float)this.field_238743_q_, 4210752);
    field_230712_o_.func_238422_b_(matrixStack, this.playerInventory.getDisplayName(), (float)this.field_238744_r_, (float)this.field_238745_s_, 4210752);

    //XXX
    //TODO: actual screen render
    field_230712_o_.func_238422_b_(matrixStack, new StringTextComponent("[WIP]AN ACTUAL SCREEN TO COME!"), 30, ySize / 2F, 0xff0000);
  }
  /*@Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);

    //XXX
    //TODO: actual screen render
    font.drawString("[WIP]AN ACTUAL SCREEN TO COME!", 30, ySize / 2F, 0xff0000);
  }*/

  @Override //drawGuiContainerBackgroundLayer //TODO: actual screen render
  protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_230706_i_.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = this.guiLeft;
    int j = this.guiTop;
    func_238474_b_(matrixStack, i, j, 0, 0, this.xSize, 3 * 18 + 17);
    func_238474_b_(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
    List<BlockState> states = getContainer().getStates();

    drawEntityOnScreen(i + xSize - 51, j + 75, 10, rotation, makeEntity(
        Blocks.STONE_BRICKS.getDefaultState()
    ));
    drawEntityOnScreen(i + xSize - 10, j + 75, 10, rotation, makeEntity(
        Blocks.OAK_FENCE.getDefaultState().with(FourWayBlock.NORTH, true)
    ));

    int k = 0;
    for (BlockState state : states) {
      drawEntityOnScreen(i + 20, j + 20 + k, 10, rotation, makeEntity(state));
      //renderBlock(matrixStack, state);
      /*drawEntityOnScreen(i+10, j+20 + k, 30, p_230450_3_, p_230450_4_,
          new FallingBlockEntity(playerInventory.player.world, 0, 0, 0, state)
      );*/
      k += 20;
    }
  }
  /*@Override //TODO: actual screen render
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(i, j, 0, 0, this.xSize, 3 * 18 + 17);
    this.blit(i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
  }*/

  private FallingBlockEntity makeEntity(BlockState state) {
    Vector3d pos = getMinecraft().player.getPositionVec();
    FallingBlockEntity entity = new FallingBlockEntity(getMinecraft().world, pos.x, pos.y, pos.z, state);
    entity.fallTime = Integer.MIN_VALUE;
    return entity;
  }
}
