package alpvax.advancedautocrafting.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

import static alpvax.advancedautocrafting.api.AAReference.MODID;

public class AAItems {
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
        /*
         * Only on Client
         */
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(AABlocks.CONTROLLER.get());
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> MULTITOOL = ITEMS.register("multitool", () -> new Item(
        new Item.Properties().tab(ITEM_GROUP)
    ));
}
