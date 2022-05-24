package alpvax.advancedautocrafting.client;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.client.gui.ControllerScreen;
import alpvax.advancedautocrafting.client.gui.RemoteMasterScreen;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import alpvax.advancedautocrafting.init.AAContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AAReference.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    static void renderWorldLastEvent(RenderLevelLastEvent evt) {
        BlockHighlightRender.render(evt.getPoseStack());
    }

    @SubscribeEvent
    static void onWorldChange(WorldEvent.Unload event) {
        BlockHighlightRender.manager.clear();
    }

    @Mod.EventBusSubscriber(modid = AAReference.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusEvents {
        @SubscribeEvent
        static void setupClient(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(AAContainerTypes.REMOTE_MASTER.get(), RemoteMasterScreen::new);
                MenuScreens.register(AAContainerTypes.CONTROLLER.get(), ControllerScreen::new);

                // Register property override for items with Position marker capability.
                // 0 = current dimension, 1 = different dimension, 2 = no position
                ItemProperties.registerGeneric(
                    new ResourceLocation(AAReference.MODID, "position_dimension"),
                    (itemStack, clientLevel, livingEntity, seed) ->
                        itemStack.getCapability(AAReference.POSITION_MARKER_CAPABILITY).map(
                            marker -> marker.matchesLevel(clientLevel) ? 0F : 1F
                        ).orElse(2F)
                );
            });
        }
    }
}
