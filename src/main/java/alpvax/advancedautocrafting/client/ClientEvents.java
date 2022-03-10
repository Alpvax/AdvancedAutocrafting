package alpvax.advancedautocrafting.client;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedAutocrafting.MODID, value = Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  static void renderWorldLastEvent(RenderLevelLastEvent evt) {
    BlockHighlightRender.render(evt.getPoseStack());
  }

  @SubscribeEvent
  static void onWorldChange(WorldEvent.Unload event) {
    BlockHighlightRender.manager.clear();
  }
}
