package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INetworkNode {
  @Nonnull UniversalPos getPos();

  @Nullable <T> INodeConnector getConnector(T context);
}
