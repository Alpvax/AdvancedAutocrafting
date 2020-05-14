package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;

public interface ICraftNetNodeFactory {
  INetworkNode create(UniversalPos position);
}
