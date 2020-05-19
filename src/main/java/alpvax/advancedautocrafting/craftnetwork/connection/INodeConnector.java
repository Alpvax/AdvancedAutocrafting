package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.UniversalPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface INodeConnector {
  enum Connectivity {
    ALLOW, BLOCK;
  }
  enum ConnectionState {
    NOT_LOADED, NO_TARGET, BLOCKED, CONNECTED;
  }

  /**
   * @return The position of the node this can connect to.
   * For adjacent connectors, will be one of the adjacent BlockPos'
   */
  @Nonnull
  UniversalPos getTargetPos();

  /**
   * @return The connector joining to this connector on the other side, or empty if there is no connector.
   * Must only be called if the other side is loaded.
   */
  @Nonnull
  Optional<INodeConnector> getTargetConnector();

  @Nonnull Connectivity getInboundConnectivity();
  @Nonnull Connectivity getOutboundConnectivity();

  default ConnectionState isConnected() {
    UniversalPos pos = getTargetPos();
    if (pos.isLoaded()) {
      return pos.getWorld().getCapability(Capabilities.NETWORK_GRAPH_CAPABILITY).map(graph -> {
        //TODO: is block networked?
        return getTargetConnector().map(c ->
          (c.getInboundConnectivity() == Connectivity.ALLOW && getOutboundConnectivity() == Connectivity.ALLOW)
              || (c.getOutboundConnectivity() == Connectivity.ALLOW && getInboundConnectivity() == Connectivity.ALLOW)
              ? ConnectionState.CONNECTED
              : ConnectionState.BLOCKED
        );
      }).orElseThrow(() -> new NullPointerException(String.format("World %s does not have the network capability", pos.getWorld())))
          .orElse(ConnectionState.NO_TARGET);
    }
    return ConnectionState.NOT_LOADED;
  }
}
