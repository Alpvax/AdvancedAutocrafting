package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class RemoteMasterScreen extends ContainerScreen<RemoteMasterContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/remote_master.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  public RemoteMasterScreen(final RemoteMasterContainer container, final PlayerInventory inventory, final ITextComponent title) {
    super(container, inventory, title);
    this.ySize = 114 + 3 * 18;
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
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(i, j, 0, 0, this.xSize, 3 * 18 + 17);
    this.blit(i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
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
