package alpvax.advancedautocrafting.craftnetwork.manager;

import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.AdjacentNodeConnectionManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

class NetworkNodeEntry {
  //final NodeManager manager;
  public final BlockPos pos;
  private INetworkNode node;
  private Set<CraftNetwork> networks = new HashSet<>();
  //private final EnumMap<Direction, Boolean> connections = new EnumMap<>(Direction.class);
  private AdjacentNodeConnectionManager connection = null;
  private boolean dirty = true;

  /*NetworkNodeEntry(NodeManager manager, BlockPos pos) {
    this.manager = manager;*/
  NetworkNodeEntry(BlockPos pos) {
    this.pos = pos;
  }

  boolean isNode() {
    return node != null;
  }

  INetworkNode getNode() {
    return node;
  }
  void setNode(INetworkNode node) {
    if (node == getNode()) {
      return;
    }
    markDirty();//TODO: Is this enough to mark removed?
    if (node == null) {
      //TODO: ?? networks.forEach(network -> network.remove(this.node));
      networks.clear();
      connection = null;
    } else {
      connection = node.createConnectionManager();
    }
    this.node = node;
    dirty = false;
  }

  NetworkNodeEntry updateIfRequired(Supplier<Optional<INetworkNode>> create) {
    if (isDirty()) {
      setNode(create.get().orElse(null));
    }
    return this;
  }

  void markDirty() {
    dirty = true;
    networks.forEach(network -> network.markDirty(this.node));
    connection.markDirty();
  }
  boolean isDirty() {
    return dirty;
  }

  Set<CraftNetwork> getNetworks() {
    return networks;
  }
  boolean connectTo(CraftNetwork network) {
    return networks.add(network);
  }
  boolean disconnect(CraftNetwork network) {
    return networks.remove(network);
  }

  boolean isConnected(Direction d) {
    return connection.childConnections()
  }

  /*void setConnection(Direction d, boolean connected) {
    connections.put(d, connected);
  }
  boolean isConnected(Direction d) {
    return isNode() && connections.computeIfAbsent(d, _d -> false);
  }*/
}
