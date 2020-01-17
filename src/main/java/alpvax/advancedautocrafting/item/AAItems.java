package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static alpvax.advancedautocrafting.AdvancedAutocrafting.MODID;

public class AAItems {
  public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

  public static final RegistryObject<Item> REMOTE_POS = ITEMS.register("remote_pos", () -> new RemotePositionItem(
      new Item.Properties()
  ));
}
