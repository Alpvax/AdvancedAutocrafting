package alpvax.advancedautocrafting.init;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AARegistration {
    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(
        Registries.LOOT_FUNCTION_TYPE, AAReference.MODID);

    public static final RegistryObject<LootItemFunctionType> POSITION_REFERENCE_LOOT = LOOT_FUNCTIONS.register(
        "position_reference",
        () -> new LootItemFunctionType(new PositionReferenceLootFunction.Serializer())
    );

    private static void createCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(AAReference.MODID, "tab"), builder ->
           // Set name of tab to display
           builder.title(Component.translatable(AATranslationKeys.CREATIVE_TAB))
               // Set icon of creative tab
               .icon(() -> new ItemStack(AABlocks.CONTROLLER.get()))
               // Add default items to tab
               .displayItems((enabledFlags, populator, hasPermissions) -> {
                   populator.accept(AABlocks.CONTROLLER.get());
                   populator.accept(AABlocks.REMOTE_MASTER.get());
                   populator.accept(AABlocks.POSITION_MARKER.get());
                   populator.accept(AABlocks.WIRE.get());
                   populator.accept(AAItems.MULTITOOL.get());
               })
        );
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(AARegistration::createCreativeTab);
        AABlocks.BLOCKS.register(modBus);
        AABlocks.Entities.BLOCK_ENTITIES.register(modBus);
        AAItems.ITEMS.register(modBus);
        AAContainerTypes.MENU_TYPES.register(modBus);
        LOOT_FUNCTIONS.register(modBus);
        AATags.init();
    }
}
