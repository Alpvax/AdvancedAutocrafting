package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class RemoteMasterScreen extends AbstractContainerScreen<RemoteMasterContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/remote_master.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  public RemoteMasterScreen(final RemoteMasterContainer container, final Inventory inventory, final Component title) {
    super(container, inventory, title);
    this.imageHeight = 114 + 3 * 18;
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
  }

  @Override
  protected void renderBg(@Nonnull PoseStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    int i = getGuiLeft();
    int j = getGuiTop();
    blit(matrixStack, i, j, 0, 0, imageWidth, 3 * 18 + 17);
    blit(matrixStack, i, j + 3 * 18 + 17, 0, 126, imageWidth, 96);
  }
  /*@Override
  protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int startX = this.guiLeft;
    int startY = this.guiTop;

    // Screen#blit draws a part of the current texture (assumed to be 256x256) to the screen
    // The parameters are (x, y, u, v, width, height)

    this.blit(startX, startY, 0, 0, this.xSize, this.ySize);

    final HeatCollectorTileEntity tileEntity = container.tileEntity;

    final SettableEnergyStorage energy = tileEntity.energy;
    final int energyStored = energy.getEnergyStored();
    if (energyStored > 0) { // Draw energy bar
      final int energyProgress = Math.round((float) energyStored / energy.getMaxEnergyStored() * 65);
      this.blit(
          startX + 152, startY + 10 + 65 - energyProgress,
          176, 14,
          14, energyProgress
      );
    }

    if (!tileEntity.inventory.getStackInSlot(HeatCollectorTileEntity.FUEL_SLOT).isEmpty()) // Draw flames
      this.blit(
          startX + 81, startY + 58,
          176, 0,
          14, 14
      );
  }*/

}
