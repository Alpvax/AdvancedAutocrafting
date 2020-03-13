package alpvax.advancedautocrafting.craftnetwork.manager;

import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

class NetworkNodeEntry {
  //final NodeManager manager;
  public final BlockPos pos;
  private INetworkNode node;
  private Set<CraftNetwork> networks = new HashSet<>();
  private final EnumMap<Direction, Boolean> connections = new EnumMap<>(Direction.class);

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
    markDirty();//TODO: Is this enough to mark removed?
    if (node == null) {
      //TODO: ?? networks.forEach(network -> network.remove(this.node));
      networks.clear();
    }
    this.node = node;
  }

  void markDirty() {
    networks.forEach(network -> network.markDirty(this.node));
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

  void setConnection(Direction d, boolean connected) {
    connections.put(d, connected);
  }
  boolean isConnected(Direction d) {
    return isNode() && connections.computeIfAbsent(d, _d -> false);
  }
}
