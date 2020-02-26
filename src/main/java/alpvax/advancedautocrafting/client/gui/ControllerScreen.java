package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ControllerScreen extends ContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  public ControllerScreen(ControllerContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);

    //XXX
    //TODO: actual screen render
    font.drawString("[WIP]AN ACTUAL SCREEN TO COME!", 30, ySize / 2F, 0xff0000);
  }

  @Override //TODO: actual screen render
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(i, j, 0, 0, this.xSize, 3 * 18 + 17);
    this.blit(i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
  }
}
