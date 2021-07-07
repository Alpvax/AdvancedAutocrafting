package alpvax.advancedautocrafting.craftnetwork.segment;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SegmentNode {

  public enum NodeType {
    /** End of spur (i.e. no children) */
    TERMINAL,
    /** Toggleable (e.g. connection which can be disconnected at will) */
    DYNAMIC,
    /** Controller/relay */
    ROOT;
  }

  private final Map<SegmentNode, Integer> connections = new HashMap<>();

  final BlockPos pos;

  SegmentNode(BlockPos pos) {
    this.pos = pos;
  }

  void addConnection(SegmentNode other, int distance) {
    connections.put(other, distance);
    other.connections.put(this, distance);
  }

  void removeConnection(SegmentNode other) {
    connections.remove(other);
    other.connections.remove(this);
  }

  /**
   * Remove all connections to/from this node, ready for deletion
   */
  void disconnect() {
    for (SegmentNode n : connections.keySet()) {
      removeConnection(n);
    }
  }

  /**
   * Remove the connections from this node and create connections between all nodes this node was connected to.
   * i.e. keep this node as part of the segment, but make it a simple wire node.
   * THIS NODE NEEDS TO BE REMOVED FROM THE SEGMENT AFTER CALLING THIS FUNCTION!
   */
  void removeAndMergeConnections() {
    Iterator<Map.Entry<SegmentNode, Integer>> i = connections.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<SegmentNode, Integer> entry = i.next();
      SegmentNode node = entry.getKey();
      int dist = entry.getValue();
      node.connections.remove(this); // Remove this connection from the other node
      for (Map.Entry<SegmentNode, Integer> e: connections.entrySet()) {
        node.addConnection(e.getKey(), e.getValue() + dist);
      }
      i.remove(); // Prevent processing each pair twice (remove connection from this node)
    }
  }
}
