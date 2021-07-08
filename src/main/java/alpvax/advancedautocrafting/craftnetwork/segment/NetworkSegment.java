package alpvax.advancedautocrafting.craftnetwork.segment;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import com.google.common.base.Preconditions;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetworkSegment {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  private final Set<BlockPos> connectedNodes = new HashSet<>();
  private final Map<BlockPos, SegmentNode> nodes = new HashMap<>();

  //public NetworkSegment() {}
  private NetworkSegment(SegmentNode node) {
    connectedNodes.add(node.pos);
    nodes.put(node.pos, node);
  }

  /**
   * Check whether a position should be connected to the segment (is adjacent to a connected position)
   * @param pos the position to check
   * @return true if adjacent
   * @throws IllegalArgumentException if pos is already part of the network
   */
  public boolean isAdjacent(@Nonnull BlockPos pos) {
    Preconditions.checkArgument(!contains(pos), "Position %s is already part of segment: %s", pos, this);
    return connectedNodes.stream().anyMatch(p -> p.distManhattan(pos) == 1);
  }

  public boolean contains(@Nonnull BlockPos pos) {
    return connectedNodes.contains(pos);
  }

  public boolean isEmpty() {
    return connectedNodes.isEmpty();
  }

  public void add(@Nonnull BlockPos pos, @Nullable INetworkNode node) {
    if (isAdjacent(pos)) {
      connectedNodes.add(pos);
      if (node != null) {
        SegmentNode n = new SegmentNode(pos); //TODO: networknode, type, etc.
        addSegment(pos, n);
      }
    }
  }

  public boolean remove(@Nonnull BlockPos pos) {
    if (contains(pos)) {
      connectedNodes.remove(pos);
      if (nodes.containsKey(pos)) {
        SegmentNode node = nodes.get(pos);
        node.disconnect();
        nodes.remove(pos); //TODO: networknode, type, etc.
      }
      return true;
    }
    return false;
  }

  private void addSegment(@Nonnull BlockPos pos, @Nonnull SegmentNode node) {
    Map<BlockPos, Integer> distances = traceToSegNodes(pos);
    for (Map.Entry<BlockPos, Integer> e : distances.entrySet()) {
      SegmentNode n = nodes.get(e.getKey());
      node.addConnection(n, e.getValue());
    }
    nodes.put(pos, node);
  }

  /**
   * Trace a path from the given position to all directly connected SegmentNodes
   * @param pos the start position
   * @return a map of position -> (distance to node if segNode, null if wire)
   */
  protected Map<BlockPos, Integer> traceToSegNodes(@Nonnull BlockPos pos) {
    Map<BlockPos, Integer> result = new HashMap<>();
    int dist = 0;
    if (contains(pos)) {
      result.put(pos, null);
    } else {
      dist ++;
      Set<BlockPos> toProcess = surroundingPositions(pos)
                                    .filter(connectedNodes::contains)
                                    .collect(Collectors.toSet());
      while (toProcess.size() > 0) {
        final int distance = dist;
        toProcess = toProcess.stream().flatMap(p -> {
          if (nodes.containsKey(p)) {
            result.put(p, distance);
            return null;
          } else {
            result.put(p, null);
            return surroundingPositions(p)
                .filter(p1 -> !result.containsKey(p1) && connectedNodes.contains(p1));
          }
        }).collect(Collectors.toSet());
        dist ++;
      }
    }
    return result;
  }

  /**
   * MODIFIES segments IN PLACE!!!
   * @param segments the segments to check for ajacency (should all be nearby, probably all in the same chunk)
   * @param pos the position of the node to add
   * @param node TODO: the node to add if not just a wire
   * @return the segments parameter, for convenience
   */
  public static Set<NetworkSegment> addNode(Set<NetworkSegment> segments, @Nonnull BlockPos pos, @Nullable INetworkNode node) {//TODO: networknode, type, etc.
    Set<NetworkSegment> combine = new HashSet<>();
    Iterator<NetworkSegment> i = segments.iterator();
    while (i.hasNext()) {
      NetworkSegment seg = i.next();
      if (seg.isEmpty()) {
        i.remove();
      } else if (seg.isAdjacent(pos)) {
        combine.add(seg);
        i.remove();
      }
    }

    SegmentNode temp = new SegmentNode(pos); // Create node to make connections to
    NetworkSegment mergedSegment = combine.stream().reduce(new NetworkSegment(temp), (merged, seg) -> {
      merged.connectedNodes.addAll(seg.connectedNodes);
      merged.nodes.putAll(seg.nodes);
      //TODO: calculate distances to temp
      //TODO: create SegNode @ pos,
      return merged;
    });
    if (node == null) {
      temp.removeAndMergeConnections();
      mergedSegment.nodes.remove(pos); // Remove wire-only node from segment nodes
    }
    return segments;
  }

  /**
   * MODIFIES segments IN PLACE!!!
   * @param segments the segments to check for ajacency (should all be nearby, probably all in the same chunk)
   * @return the segments parameter, for convenience
   */
  public static Set<NetworkSegment> reduce(Set<NetworkSegment> segments) {
    segments.removeIf(NetworkSegment::isEmpty);
    return segments;
  }

  private static Stream<BlockPos> surroundingPositions(@Nonnull BlockPos startPos) {
    return Arrays.stream(ALL_DIRECTIONS).map(startPos::relative);
  }
}
