package alpvax.advancedautocrafting.api;

import alpvax.advancedautocrafting.api.util.IPositionReference;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class AAReference {
    public static final String MODID = "advancedautocrafting";

    // CAPABILITIES
    public static net.minecraftforge.common.capabilities.Capability<INetworkNode> NODE_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});
    public static net.minecraftforge.common.capabilities.Capability<IPositionReference> POSITION_MARKER_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});
}
