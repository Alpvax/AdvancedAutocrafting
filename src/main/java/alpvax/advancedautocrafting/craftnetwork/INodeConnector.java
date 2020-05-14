package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.graph.NetworkGraph;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
  @Nonnull UniversalPos getTargetPos();

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
      AtomicReference<ConnectionState> result = new AtomicReference<>(ConnectionState.NO_TARGET);
      LazyOptional<NetworkGraph> cap = pos.getWorld().getCapability(Capabilities.NETWORK_GRAPH_CAPABILITY);
      cap.ifPresent(graph -> {
        //TODO: is block networked?
        Optional<INodeConnector> conn = getTargetConnector();
        if (conn.isPresent()) {
          INodeConnector c = conn.get();
          result.set((c.getInboundConnectivity() == Connectivity.ALLOW && getOutboundConnectivity() == Connectivity.ALLOW)
                     || (c.getOutboundConnectivity() == Connectivity.ALLOW && getInboundConnectivity() == Connectivity.ALLOW)
              ? ConnectionState.CONNECTED : ConnectionState.BLOCKED);
        }
      });
      return result.get();
    }
    return ConnectionState.NOT_LOADED;
  }
}
