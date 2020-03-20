package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.manager.NodeManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AdjacentNodeConnectionManager {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  private final INetworkNode node;
  private final IWorldReader world;
  private final BlockPos pos;
  private boolean isDirty = true;
  private EnumMap<Direction, Boolean> inbound = new EnumMap<>(Direction.class);
  private EnumMap<Direction, Boolean> outbound = new EnumMap<>(Direction.class);
  private final EnumMap<Direction, AdjacentNodeConnectionManager> parents = new EnumMap<>(Direction.class);
  private final EnumMap<Direction, AdjacentNodeConnectionManager> children = new EnumMap<>(Direction.class);

  public AdjacentNodeConnectionManager(INetworkNode node) {
    this.node = node;
    this.world = node.getWorld();
    this.pos = node.getPos();
  }

  public void markDirty() {
    isDirty = true;
    parents.clear();
    children.clear();
  }

  protected void updateCache() {
    parents.clear();
    children.clear();
    for (Direction d : ALL_DIRECTIONS) {
      getNeighbourParent(d).ifPresent(n -> parents.put(d, n));
      getNeighbourChild(d).ifPresent(n -> children.put(d, n));
    }
    isDirty = false;
  }

  @Nonnull
  protected final EnumMap<Direction, AdjacentNodeConnectionManager> getNeighbourParents() {
    if (isDirty) {
      updateCache();
    }
    return parents;
  }
  @Nonnull
  protected final EnumMap<Direction, AdjacentNodeConnectionManager> getNeighbourChildren() {
    if (isDirty) {
      updateCache();
    }
    return children;
  }

  @Nonnull
  protected Optional<AdjacentNodeConnectionManager> getNeighbourChild(Direction d) {
    return getNeighbourRaw(d).filter(ancm -> canConnectTo(d.getOpposite()));
  }
  @Nonnull
  protected Optional<AdjacentNodeConnectionManager> getNeighbourParent(Direction d) {
    return getNeighbourRaw(d).filter(ancm -> canConnectFrom(d.getOpposite()));
  }
  private Optional<AdjacentNodeConnectionManager> getNeighbourRaw(Direction d) {
    return NodeManager.getNodeAt(world, pos.offset(d))
               .map(AdjacentNodeConnectionManager::new /*TODO: Implement correctly*/);
  }

  public boolean canConnectFrom(Direction fromDir) {
    return inbound.getOrDefault(fromDir, true);
  }
  public boolean canConnectTo(Direction dir) {
    return outbound.getOrDefault(dir, true);
  }

  public void setConnectionState(Direction d, boolean enabled) {
    setConnectionState(d, enabled, enabled);
  }
  public void setConnectionState(Direction d, boolean parentEnabled, boolean childEnabled) {
    setParentConnectionState(d, parentEnabled);
    setChildConnectionState(d, childEnabled);
  }
  public void setParentConnectionState(Direction d, boolean enabled) {
    inbound.put(d, enabled);
    markDirty();
  }
  public void setChildConnectionState(Direction d, boolean enabled) {
    outbound.put(d, enabled);
    markDirty();
  }

  public Set<DirectNodeConnection> childConnections() {
    Set<DirectNodeConnection> connections = new HashSet<>();
    getNeighbourChildren().forEach((d, c) -> {
      connections.add(new DirectNodeConnection(node, c.node, d));
    });
    return connections;
  }
}
