package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.block.wire.parts.None;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class WirePartsRegistry {
    public static final DeferredRegister<IWirePart<?,?>> PARTS =
        DeferredRegister.create(AAReference.WIRE_PARTS, AAReference.MODID);

    public static final RegistryObject<IWirePart<?,?>> NONE = PARTS.register("none", None::new);

    private static void createRegistry(NewRegistryEvent event) {
        event.create(
            new RegistryBuilder<>()
                .setName(AAReference.WIRE_PARTS.location())
                .setDefaultKey(NONE.getId())
        );
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(WirePartsRegistry::createRegistry);
        PARTS.register(modBus);
    }
}
