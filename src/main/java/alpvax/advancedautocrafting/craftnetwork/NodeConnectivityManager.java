package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.api.craftnetwork.NodeConnectivity;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class NodeConnectivityManager {
    private static final Map<ResourceLocation, NodeConnectivity.IBlockStateConnectivityMapper> blockStateFactory = new HashMap<>();

    public static void registerBlockstateConnectivityFactory(
        ResourceLocation blockId,
        NodeConnectivity.IBlockStateConnectivityMapper factory) {
        blockStateFactory.put(blockId, factory);
    }
}
