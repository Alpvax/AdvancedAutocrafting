package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class ControllerScreen extends ContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");
  private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  public ControllerScreen(ControllerContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    renderHoveredTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
    font.func_238422_b_(matrixStack, title.func_241878_f(), (float)titleX, (float)titleY, 4210752);
    font.func_238422_b_(matrixStack, this.playerInventory.getDisplayName().func_241878_f(), (float)playerInventoryTitleX, (float)playerInventoryTitleY, 4210752);

    //XXX
    //TODO: actual screen render
    font.func_238422_b_(matrixStack, new StringTextComponent("[WIP]AN ACTUAL SCREEN TO COME!").func_241878_f(), 30, ySize / 2F, 0xff0000);
  }

  @Override //TODO: actual screen render
  protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = this.guiLeft;
    int j = this.guiTop;
    blit(matrixStack, i, j, 0, 0, this.xSize, 3 * 18 + 17);
    blit(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
  }
}
