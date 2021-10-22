package alpvax.advancedautocrafting.craftnetwork.impl;

import java.util.Optional;

interface INodeConnector {

  /**
   * Should return true even if the connected path is disabled or not loaded, as long as there is another node connected, should return true
   * @return whether or not this connector is attached to another.
   */
  boolean isConnected();

  Optional<INetworkPath> getPath();
}
