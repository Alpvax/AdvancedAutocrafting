package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ControllerScreen extends AbstractContainerScreen<ControllerContainer> {
    //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(
        "textures/gui/container/generic_54.png");

    public ControllerScreen(ControllerContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

//    @Override
//    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
//        renderBackground(matrixStack);
//        super.render(matrixStack, mouseX, mouseY, partialTicks);
//        renderTooltip(matrixStack, mouseX, mouseY);
//    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        //XXX
        //TODO: actual screen render
        graphics.drawString(font, "[WIP]AN ACTUAL SCREEN TO COME!", 30, imageHeight / 2F, 0xff0000, false);
    }

    @Override //TODO: actual screen render
    protected void renderBg(GuiGraphics graphics, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        int i = getGuiLeft();
        int j = getGuiTop();
        graphics.blit(BACKGROUND_TEXTURE, i, j, 0, 0, imageWidth, 3 * 18 + 17);
        graphics.blit(BACKGROUND_TEXTURE, i, j + 3 * 18 + 17, 0, 126, imageWidth, 96);
    }
}
