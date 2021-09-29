package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ControllerScreen extends AbstractContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  public ControllerScreen(ControllerContainer container, Inventory inv, Component title) {
    super(container, inv, title);
  }

  @Override
  public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    renderTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(@Nonnull PoseStack matrixStack, int mouseX, int mouseY) {
    font.draw(matrixStack, title, (float)titleLabelX, (float)titleLabelY, 4210752);
    font.draw(matrixStack, this.playerInventoryTitle, (float)inventoryLabelX, (float)inventoryLabelY, 4210752);

    //XXX
    //TODO: actual screen render
    font.draw(matrixStack, new TextComponent("[WIP]AN ACTUAL SCREEN TO COME!"), 30, imageHeight / 2F, 0xff0000);
  }

  @Override //TODO: actual screen render
  protected void renderBg(@Nonnull PoseStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    int i = getGuiLeft();
    int j = getGuiTop();
    blit(matrixStack, i, j, 0, 0, this.imageWidth, 3 * 18 + 17);
    blit(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.imageWidth, 96);
  }
}
