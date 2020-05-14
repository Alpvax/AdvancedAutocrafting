package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INetworkNode<T> {
  @Nonnull UniversalPos getPos();

  @Nullable INodeConnector getConnector(T context);
}
