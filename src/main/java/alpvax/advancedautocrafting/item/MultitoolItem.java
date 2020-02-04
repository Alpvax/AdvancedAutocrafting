package alpvax.advancedautocrafting.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class MultitoolItem extends Item {
  public MultitoolItem(Item.Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return super.initCapabilities(stack, nbt); //TODO: return other mod wrench capabilities
  }
}
