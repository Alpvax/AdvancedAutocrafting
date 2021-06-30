package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
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
  public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    BlockPosLootFunction.WorldPosPair data = BlockPosLootFunction.read(stack);
    if (data.valid()) {
      tooltip.add(new TranslationTextComponent(AATranslationKeys.ITEM_POS_LORE, data.getPos()).withStyle(TextFormatting.GRAY));
      if (flagIn.isAdvanced() || !data.matchesWorld(worldIn)) {
        tooltip.add(new TranslationTextComponent(AATranslationKeys.ITEM_DIM_LORE, data.getWorldID()).withStyle(TextFormatting.GRAY));
      }
    }
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    BlockPosLootFunction.WorldPosPair data = BlockPosLootFunction.read(stack);
    if (data.matchesWorld(world)) {
      if (world.isClientSide()) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
          BlockPos pos = data.getPos();
          if (BlockHighlightRender.manager.contains(pos)) {
            BlockHighlightRender.manager.remove(pos);
          } else {
            BlockHighlightRender.manager.add(pos, 69, 120, 18, 160);
          }
        });
        return ActionResult.success(stack);
      }
    }
    return ActionResult.pass(stack);
  }

  @Override
  public boolean isFoil(@Nonnull ItemStack stack) {
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
