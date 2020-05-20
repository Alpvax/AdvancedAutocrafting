package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface INetworkNodeInstance {
  UUID getNodeID();
  boolean isJunction();

  ResourceLocation getType();
}
