package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.AAUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class RemotePositionItem extends Item {

  public RemotePositionItem(Properties properties) {
    super(properties);
  }

  /**
   * allows items to add custom lines of information to the mouseover description
   */
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(AAUtil.getItemPositionText(stack).applyTextStyle(TextFormatting.GRAY));
  }
}
