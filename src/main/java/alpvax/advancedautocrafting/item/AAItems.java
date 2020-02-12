package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.block.AABlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import static alpvax.advancedautocrafting.AdvancedAutocrafting.MODID;

public class AAItems {
  public static final ItemGroup ITEM_GROUP = (new ItemGroup(MODID) {
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
      return new ItemStack(AABlocks.CONTROLLER.get());
    }
  });

  public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

  public static final RegistryObject<Item> REMOTE_POS = ITEMS.register("remote_pos", () -> new RemotePositionItem(
      new Item.Properties()
  ));
  public static final RegistryObject<MultitoolItem> MULTITOOL = ITEMS.register("multitool", () -> new MultitoolItem(
      new Item.Properties().group(ITEM_GROUP)
  ));
}
