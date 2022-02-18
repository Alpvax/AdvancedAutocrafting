package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.block.AABlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static alpvax.advancedautocrafting.AdvancedAutocrafting.MODID;

public class AAItems {
  public static final CreativeModeTab ITEM_GROUP = (new CreativeModeTab(MODID) {
    /*
     * Only on Client
     */
    public ItemStack makeIcon() {
      return new ItemStack(AABlocks.CONTROLLER.get());
    }
  });

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

  public static final RegistryObject<Item> REMOTE_POS = ITEMS.register("remote_pos", () -> new RemotePositionItem(
      new Item.Properties()
  ));
  public static final RegistryObject<MultitoolItem> MULTITOOL = ITEMS.register("multitool", () -> new MultitoolItem(
      new Item.Properties().tab(ITEM_GROUP)
  ));
}
