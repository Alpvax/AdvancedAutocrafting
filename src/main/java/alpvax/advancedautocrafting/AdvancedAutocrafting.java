package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.api.AAIMCHelper;
import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.api.util.IPositionReference;
import alpvax.advancedautocrafting.client.data.AABlockstateProvider;
import alpvax.advancedautocrafting.client.data.AAItemModelProvider;
import alpvax.advancedautocrafting.client.data.AALangProvider;
import alpvax.advancedautocrafting.data.AALootTableProvider;
import alpvax.advancedautocrafting.data.AARecipeProvider;
import alpvax.advancedautocrafting.data.AATagsProvider;
import alpvax.advancedautocrafting.init.AARegistration;
import alpvax.advancedautocrafting.network.AAPacketManager;
import net.minecraft.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


@Mod(AAReference.MODID)
public class AdvancedAutocrafting {
    private static final Logger LOGGER = LogManager.getLogger();

    public AdvancedAutocrafting() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // General mod setup
        modBus.addListener(this::setup);
        modBus.addListener(this::gatherData);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        // Registry objects
        AARegistration.init(modBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        AAPacketManager.registerPackets();
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(INetworkNode.class);
        event.register(IPositionReference.class);
    }

    private void onServerStarting(final ServerStartingEvent event) {
        //Register commands
        //CommandTropicsTeleport.register(event.getServer().getCommandManager().getDispatcher());
    }

    private static final Map<String, AAIMCHelper.IMCMethod> imcMethodFromName = Util.make(new HashMap<>(), map -> {
        for (AAIMCHelper.IMCMethod method : AAIMCHelper.IMCMethod.values()) {
            map.put(method.getSerializedName(), method);
        }
    });
    private void processIMC(final InterModProcessEvent event) {
        event.getIMCStream().forEach(msg -> {
            //TODO: Remove warning suppression if/when more IMC types are added
            //noinspection SwitchStatementWithTooFewBranches
            switch (imcMethodFromName.get(msg.method())) {
                case REGISTER_CONNECTIVITY -> {
                    /*
                    TODO: @SuppressWarnings("unchecked")
                    Pair<ResourceLocation, NodeConnectivity.IBlockStateConnectivityMapper> value =
                        (Pair<ResourceLocation, NodeConnectivity.IBlockStateConnectivityMapper>)
                            msg.messageSupplier().get();
                    NodeConnectivityManager.registerBlockstateConnectivityFactory(value.getLeft(), value.getRight());
                     */
                }
                default -> LOGGER.warn(
                    "Recieved IMC message from mod \"{}\" with invalid method: \"{}\"", msg.senderModId(),
                    msg.method()
                );
            }
        });
    }

    private void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var output = gen.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var efh = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new AABlockstateProvider(output, efh));
        gen.addProvider(event.includeClient(), new AAItemModelProvider(output, efh));
        gen.addProvider(event.includeClient(), new AALangProvider(output));

        AATagsProvider.addProviders(gen, lookupProvider, event.includeServer(), efh);
        gen.addProvider(event.includeServer(), new AARecipeProvider(output));
        gen.addProvider(event.includeServer(), new AALootTableProvider(output));
    }
}