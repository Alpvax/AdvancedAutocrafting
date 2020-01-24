package alpvax.advancedautocrafting.client;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedAutocrafting.MODID, value = Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  static void renderWorldLastEvent(RenderWorldLastEvent evt) {
    /*PlayerEntity player = Minecraft.getInstance().player;
    Stream<BlockPos> pos = Arrays.stream(Hand.values()).map(player::getHeldItem).map(AAUtil::readPosFromItemStack);
    Set<BlockPos> set =  pos
        //.filter(Objects::nonNull)
        .collect(Collectors.toSet());
    BlockHighlightRender.render(set, evt.getMatrixStack());*/
    BlockHighlightRender.render(evt.getMatrixStack());
  }

  @SubscribeEvent
  static void onWorldChange(WorldEvent.Unload event) {
    BlockHighlightRender.manager.clear();
  }
}
