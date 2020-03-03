package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;

public interface INodeConnection<T extends INodeConnection<T>> {
  int transferCost();
  INetworkNode getParent();
  INetworkNode getChild();
  T invert();
}
