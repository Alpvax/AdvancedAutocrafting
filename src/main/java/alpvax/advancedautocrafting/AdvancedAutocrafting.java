package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.client.data.AABlockstateProvider;
import alpvax.advancedautocrafting.client.data.AAItemModelProvider;
import alpvax.advancedautocrafting.client.data.AALangProvider;
import alpvax.advancedautocrafting.client.gui.ControllerScreen;
import alpvax.advancedautocrafting.client.gui.RemoteMasterScreen;
import alpvax.advancedautocrafting.container.AAContainerTypes;
import alpvax.advancedautocrafting.data.AALootTableProvider;
import alpvax.advancedautocrafting.data.AARecipeProvider;
import alpvax.advancedautocrafting.data.AATags;
import alpvax.advancedautocrafting.data.AATagsProvider;
import alpvax.advancedautocrafting.data.BlockPosLootFunction;
import alpvax.advancedautocrafting.item.AAItems;
import alpvax.advancedautocrafting.network.AAPacketManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
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
    modBus.addListener(Capabilities::register);

    // Loot Table registering
    BlockPosLootFunction.register();
    //LootFunctionManager.registerFunction(new BlockPosLootFunction.Serializer());

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
      // Client setup
      modBus.addListener(this::setupClient);
    });

    MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

    // Registry objects
    AABlocks.BLOCKS.register(modBus);
    AABlocks.Entities.BLOCK_ENTITIES.register(modBus);
    AAItems.ITEMS.register(modBus);
    AAContainerTypes.CONTAINER_TYPES.register(modBus);

    AATags.init();
  }

  private void setup(final FMLCommonSetupEvent event) {
    AAPacketManager.registerPackets();
  }

  /*
   * Only on Client
   */
  private void setupClient(final FMLClientSetupEvent event) {
    //ClientRegistry.bindTileEntitySpecialRenderer(DrinkMixerTileEntity.class, new DrinkMixerRenderer());
    event.enqueueWork(() -> {
      MenuScreens.register(AAContainerTypes.REMOTE_MASTER.get(), RemoteMasterScreen::new);
      MenuScreens.register(AAContainerTypes.CONTROLLER.get(), ControllerScreen::new);
    });
  }

  private void onServerStarting(final ServerStartingEvent event) {
    //Register commands
    //CommandTropicsTeleport.register(event.getServer().getCommandManager().getDispatcher());
  }

  private void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper efh = event.getExistingFileHelper();

    if (event.includeClient()) {
      gen.addProvider(new AABlockstateProvider(gen, efh));
      gen.addProvider(new AAItemModelProvider(gen, efh));
      gen.addProvider(new AALangProvider(gen));
    }
    if (event.includeServer()) {
      AATagsProvider.addProviders(gen, efh);
      gen.addProvider(new AARecipeProvider(gen));
      gen.addProvider(new AALootTableProvider(gen));
    }
  }
}