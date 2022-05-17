package alpvax.advancedautocrafting.init;

import alpvax.advancedautocrafting.api.AAReference;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AARegistration {
    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, AAReference.MODID);

    public static final RegistryObject<LootItemFunctionType> POSITION_REFERENCE_LOOT = LOOT_FUNCTIONS.register("position_reference",
        () -> new LootItemFunctionType(new PositionReferenceLootFunction.Serializer())
    );

    public static void init(IEventBus modBus) {
        AABlocks.BLOCKS.register(modBus);
        AABlocks.Entities.BLOCK_ENTITIES.register(modBus);
        AAItems.ITEMS.register(modBus);
        AAContainerTypes.CONTAINER_TYPES.register(modBus);

        AATags.init();
    }
}
