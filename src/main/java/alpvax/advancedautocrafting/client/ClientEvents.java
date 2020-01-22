package alpvax.advancedautocrafting.client;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = AdvancedAutocrafting.MODID, value = Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  static void renderWorldLastEvent(RenderWorldLastEvent evt) {
    PlayerEntity player = Minecraft.getInstance().player;
    Stream<BlockPos> pos = Arrays.stream(Hand.values()).map(player::getHeldItem).map(AAUtil::readPosFromItemStack);
    Set<BlockPos> set =  pos
        //.filter(Objects::nonNull)
        .collect(Collectors.toSet());
    BlockHighlightRender.render(set, evt.getMatrixStack());
  }
}
