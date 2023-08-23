package alpvax.advancedautocrafting.api;

import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.api.util.IPositionReference;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class AAReference {
    public static final String MODID = "advancedautocrafting";

    public static final ResourceKey<Registry<IWirePart<?,?>>> WIRE_PARTS =
        ResourceKey.createRegistryKey(new ResourceLocation(MODID, "wire_parts"));

    // CAPABILITIES
    public static net.minecraftforge.common.capabilities.Capability<INetworkNode> NODE_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});
    public static net.minecraftforge.common.capabilities.Capability<IPositionReference> POSITION_MARKER_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});

    public static class Wire {
        public static final float CORE_RADIUS =  3 / 16F;
    }
}
