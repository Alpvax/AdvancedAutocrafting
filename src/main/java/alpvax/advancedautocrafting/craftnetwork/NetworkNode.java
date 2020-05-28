package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;
import alpvax.advancedautocrafting.util.UniversalPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class NetworkNode {
  private final UniversalPos pos;
  private final Map<INodeConnector, INodeConnection> connectors = new HashMap<>();

  public NetworkNode(UniversalPos pos) {
    this.pos = pos;
    initConnectors().forEach(c -> connectors.put(c, INodeConnection.NOT_CONNECTED));
  }

  /**
   * @return all valid outbound node connectors
   */
  protected abstract Set<INodeConnector> initConnectors();

  public Set<NetworkNode> getLoadedChildren() {
    return connectors.values().stream()
               .filter(INodeConnection::isValid)
               .filter(INodeConnection::isLoaded)
               .map(INodeConnection::getTargetNode)
               .collect(Collectors.toSet());
  }
}
