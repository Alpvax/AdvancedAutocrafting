package alpvax.advancedautocrafting.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static alpvax.advancedautocrafting.api.AAReference.MODID;

public class AAItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> MULTITOOL = ITEMS.register("multitool", () -> new Item(
        new Item.Properties()
    ));
}
