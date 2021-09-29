package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.item.IMultitool;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities {
  @CapabilityInject(INetworkNode.class)
  public static Capability<INetworkNode> NODE_CAPABILITY = null;

  @CapabilityInject(IMultitool.class)
  public static Capability<IMultitool> MULTITOOL_CAPABILITY = null;

  public static void register(RegisterCapabilitiesEvent event) {
    event.register(INetworkNode.class);
    event.register(IMultitool.class);
  }
}
