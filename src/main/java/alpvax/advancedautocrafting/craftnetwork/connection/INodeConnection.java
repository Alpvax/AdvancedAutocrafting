package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.NetworkNode;
import alpvax.advancedautocrafting.util.UniversalPos;

public interface INodeConnection {
  INodeConnection NOT_CONNECTED = new INodeConnection() {
    @Override
    public UniversalPos getTargetPos() {
      return null;
    }
    @Override
    public boolean isValid() {
      return false;
    }
    @Override
    public boolean isLoaded() {
      return false;
    }
  };

  /*@Nonnull UniversalPos getSourceConnector();*/
  UniversalPos getTargetPos();
  boolean isValid();
  boolean isLoaded();
  default NetworkNode getTargetNode() {
    if (!isValid()) {
      throw new NullPointerException("Tried to retrieve target of invalid connection: " + this);
    }
    if (!isLoaded()) {
      //TODO: Load?
    }
    //TODO: get node from position
    return null;
  }
}
