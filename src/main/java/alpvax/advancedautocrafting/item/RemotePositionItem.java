package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;

public class RemotePositionItem extends Item {

  public RemotePositionItem(Properties properties) {
    super(properties);
  }

  /**
   * allows items to add custom lines of information to the mouseover description
   */
  /*
   * Only on Client
   */
  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    BlockPosLootFunction.WorldPosPair data = BlockPosLootFunction.read(stack);
    if (data.valid()) {
      tooltip.add(new TranslatableComponent(AATranslationKeys.ITEM_POS_LORE, data.getPos()).withStyle(ChatFormatting.GRAY));
      if (flagIn.isAdvanced() || !data.matchesLevel(worldIn)) {
        tooltip.add(new TranslatableComponent(AATranslationKeys.ITEM_DIM_LORE, data.getWorldID()).withStyle(ChatFormatting.GRAY));
      }
    }
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    BlockPosLootFunction.WorldPosPair data = BlockPosLootFunction.read(stack);
    if (data.matchesLevel(world)) {
      if (world.isClientSide()) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
          BlockPos pos = data.getPos();
          if (BlockHighlightRender.manager.contains(pos)) {
            BlockHighlightRender.manager.remove(pos);
          } else {
            BlockHighlightRender.manager.add(pos, 69, 120, 18, 160);
          }
        });
        return InteractionResultHolder.consume(stack);
      }
    }
    return InteractionResultHolder.pass(stack);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return DistExecutor.unsafeRunForDist(() -> () -> isRendering(stack), () -> () -> super.isFoil(stack));
  }

  /*
   * Only on Client
   */
  private boolean isRendering(ItemStack stack) {
    BlockPosLootFunction.WorldPosPair pair = BlockPosLootFunction.read(stack);
    return pair.valid() && BlockHighlightRender.manager.contains(pair.getPos());
  }
}
