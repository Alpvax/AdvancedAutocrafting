package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities {
  public static Capability<INetworkNode> NODE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

  public static void register(RegisterCapabilitiesEvent event) {
    event.register(INetworkNode.class);
  }
}
