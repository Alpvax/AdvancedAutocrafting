package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

class NetworkNode {
  protected static final Direction[] ALL_DIRECTIONS = Direction.values();

  public final IWorldReader world;
  public final BlockPos pos;
  private final EnumMap<Direction, Boolean> connections = new EnumMap<>(Direction.class);
  private final Set<INodeChangeListener> listeners =  new HashSet<>();

  public NetworkNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof NetworkNode) {
      NetworkNode node = (NetworkNode)obj;
      return world.equals(node.world) && pos.equals(node.pos);
    }
    return false;
  }

  public NetworkNode addListener(@Nonnull INodeChangeListener listener) {
    listeners.add(listener);
    return this;
  }
  public void removeListener(@Nonnull INodeChangeListener listener) {
    listeners.remove(listener);
  }

  boolean canConnect(Direction d) {
    return true;
  }

  void connect(Direction d, boolean connect) {
    boolean prev = isConnected(d);
    if (prev != connect) {
      connections.put(d, connect);
      listeners.forEach(l -> l.onConnectionChanged(this, d));
    }
  }

  boolean isConnected(Direction d) {
    return connections.computeIfAbsent(d, k -> false);
  }

  public Stream<Direction> connectedDirections() {
    return connections.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey);
  }

  private int numConnections() {
    return (int) connectedDirections().count();
  }

  public EnumMap<Direction, NetworkNode> getConnections() {
    EnumMap<Direction, NetworkNode> map = new EnumMap<>(Direction.class);
    connectedDirections().forEach(d -> map.put(d, getAdjacent(d)));
    return map;
  }

  public boolean isIsolated() {
    return numConnections() < 1;
  }

  public boolean isJunction() {
    return numConnections() > 2;
  }

  public NetworkNode getAdjacent(Direction d) {
    return isConnected(d)
               //TODO: implement getting adjacent nodes
               ? new NetworkNode(world, pos.offset(d))
               : null;
  }

  void neighbourAdded(BlockPos neighbourPos) {
    //TODO:
  }
  void neighbourRemoved(BlockPos neighbourPos) {
    //TODO:
  }

  void onAdd() {
    listeners.forEach(l -> l.onAdded(this));
  }
  void onRemove() {
    listeners.forEach(l -> l.onRemoved(this));
  }
}
