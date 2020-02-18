package alpvax.advancedautocrafting.item.multitool;

import net.minecraft.item.ItemStack;

public abstract class MultiToolType {

  abstract boolean isValid(ItemStack stack);

  public static final MultiToolType TOOL = new MultiToolType() {
    @Override
    boolean isValid(ItemStack stack) {
      return stack.getItem().getToolTypes(stack).size() > 0;
    }
  };
}
