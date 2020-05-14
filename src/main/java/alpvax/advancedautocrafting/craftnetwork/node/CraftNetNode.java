package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftNetNode implements INetworkNode {

  public CraftNetNode() {
  }

  @Nonnull
  @Override
  public UniversalPos getPos() {
    return null;
  }

  @Nullable
  @Override
  public INodeConnector getConnector(Object context) {
    return null;
  }
}
