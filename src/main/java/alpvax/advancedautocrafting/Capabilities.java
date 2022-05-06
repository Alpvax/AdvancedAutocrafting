package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.util.IPositionReference;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities {
    public static Capability<INetworkNode> NODE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static Capability<IPositionReference> POSITION_MARKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(INetworkNode.class);
        event.register(IPositionReference.class);
    }
}
