package alpvax.advancedautocrafting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedAutocrafting.MODID)
public class AdvancedAutocrafting {
  public static final String MODID = "advancedautocrafting";

  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public AdvancedAutocrafting() {
    MinecraftForge.EVENT_BUS.register(this);
    //DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientProxy.init());
    //PacketHandler.register();
  }
}