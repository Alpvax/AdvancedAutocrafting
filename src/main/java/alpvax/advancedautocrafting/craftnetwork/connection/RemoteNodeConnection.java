package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;

public class RemoteNodeConnection implements INodeConnection<RemoteNodeConnection> {
  private final INetworkNode parent;
  private final INetworkNode child;

  public RemoteNodeConnection(INetworkNode parent, INetworkNode child) {
    this.parent = parent;
    this.child = child;
  }
  @Override
  public int transferCost() {
    //TODO: Config: distSquared or manhattan*2 or manhattan*3
    return getParent().getPos().manhattanDistance(getChild().getPos()) * 2;
  }

  @Override
  public INetworkNode getParent() {
    return parent;
  }

  @Override
  public INetworkNode getChild() {
    return child;
  }

  @Override
  public RemoteNodeConnection invert() {
    return new RemoteNodeConnection(child, parent);
  }
}
