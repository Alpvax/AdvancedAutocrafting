package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.client.data.AABlockstateProvider;
import alpvax.advancedautocrafting.client.data.AAItemModelProvider;
import alpvax.advancedautocrafting.client.data.AALangProvider;
import alpvax.advancedautocrafting.client.gui.ControllerScreen;
import alpvax.advancedautocrafting.client.gui.RemoteMasterScreen;
import alpvax.advancedautocrafting.container.AAContainerTypes;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.ISimpleCraftNetworkNodeFactory;
import alpvax.advancedautocrafting.data.AALootTableProvider;
import alpvax.advancedautocrafting.data.AARecipeProvider;
import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(AdvancedAutocrafting.MODID)
public class AdvancedAutocrafting {
  public static final String MODID = "advancedautocrafting";

  private static final Logger LOGGER = LogManager.getLogger();

  public AdvancedAutocrafting() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    // General mod setup
    modBus.addListener(this::setup);
    modBus.addListener(this::gatherData);

    // Loot Table registering
    LootFunctionManager.registerFunction(new BlockPosLootFunction.Serializer());

    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
      // Client setup
      modBus.addListener(this::setupClient);
    });

    MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
    Capabilities.registerAttachEvents();

    // Registry objects
    AABlocks.BLOCKS.register(modBus);
    AABlocks.TileTypes.TILES.register(modBus);
    AAItems.ITEMS.register(modBus);
    AAContainerTypes.CONTAINER_TYPES.register(modBus);
  }

  private void setup(final FMLCommonSetupEvent event) {
    //PacketManager.init();

    Capabilities.register();
  }

  @OnlyIn(Dist.CLIENT)
  private void setupClient(final FMLClientSetupEvent event) {
    //ClientRegistry.bindTileEntitySpecialRenderer(DrinkMixerTileEntity.class, new DrinkMixerRenderer());
    ScreenManager.registerFactory(AAContainerTypes.REMOTE_MASTER.get(), RemoteMasterScreen::new);
    ScreenManager.registerFactory(AAContainerTypes.CONTROLLER.get(), ControllerScreen::new);
  }

  private void onServerStarting(final FMLServerStartingEvent event) {
    //Register commands
    //CommandTropicsTeleport.register(event.getServer().getCommandManager().getDispatcher());
  }

  private void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();

    if (event.includeClient()) {
      gen.addProvider(new AABlockstateProvider(gen, event.getExistingFileHelper()));
      //TODO: Generate item models when supported by forge:
      gen.addProvider(new AAItemModelProvider(gen, event.getExistingFileHelper()));
      gen.addProvider(new AALangProvider(gen));
    }
    if (event.includeServer()) {
      /*
      gen.addProvider(new TropicraftBlockTagsProvider(gen));
      gen.addProvider(new TropicraftItemTagsProvider(gen));
      */
      gen.addProvider(new AARecipeProvider(gen));
      gen.addProvider(new AALootTableProvider(gen));
    }
  }

  /*
  private void enqueIMC(InterModEnqueueEvent event) {
    Example registration of custom simple block (a block without a TileEntity, and therefore no capabilities)
    InterModComms.sendTo(MODID, () -> Pair.of(AABlocks.WIRE.getId(), (state, world, pos) -> new NetworkNodeImpl()));
  }*/

  private void processIMC(InterModProcessEvent event) {
    event.getIMCStream("registersimpletile"::equalsIgnoreCase)
        .forEach(msg -> {
      Pair<ResourceLocation, ISimpleCraftNetworkNodeFactory> val = msg.<Pair<ResourceLocation, ISimpleCraftNetworkNodeFactory>>getMessageSupplier().get();
      INetworkNode.NON_TILE_NODES.put(val.getKey(), val.getValue());
    });
  }
}