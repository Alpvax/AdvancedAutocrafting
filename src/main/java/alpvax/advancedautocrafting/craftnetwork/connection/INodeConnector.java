package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.util.UniversalPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface INodeConnector {
  enum ConnectionState {
    NOT_LOADED, NO_TARGET, BLOCKED, CONNECTED;
  }

  /**
   * Will be called to begin creating a connection.
   * @return The position of the node this can connect to.
   * For adjacent connectors, will be one of the adjacent BlockPos'
   */
  @Nonnull
  UniversalPos findTarget();

  /*@Nonnull
  ConnectionAttempt makeConnection();*/

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