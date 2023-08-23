package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RemoteMasterScreen extends AbstractContainerScreen<RemoteMasterContainer> {
    //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/remote_master.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(
        "textures/gui/container/generic_54.png");

    public RemoteMasterScreen(final RemoteMasterContainer container, final Inventory inventory, final Component title) {
        super(container, inventory, title);
        imageHeight = 114 + 3 * 18;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int i = getGuiLeft();
        int j = getGuiTop();
        graphics.blit(BACKGROUND_TEXTURE, i, j, 0, 0, imageWidth, 3 * 18 + 17);
        graphics.blit(BACKGROUND_TEXTURE, i, j + 3 * 18 + 17, 0, 126, imageWidth, 96);
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
