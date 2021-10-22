package alpvax.advancedautocrafting.craftnetwork.impl;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ChunkGraph {
  private final ChunkPos position;
  private final Map<Node, ? /*TODO: Node data*/> nodes = new HashMap<>();
  /** Map of horz direction to available nodes*/
  private final Map<Direction, Set<Node>> externalConnectors = new HashMap<>();
  private final Set<Node> junctions = new HashSet<>();

  public ChunkGraph(ChunkPos position) {
    this.position = position;
  }

  private boolean containsNode(Node n) {
    return position.equals(new ChunkPos(n.getX(), n.getZ()));
  }

  /**
   * Local position within this chunk.
   *
   * Comparison order is y -> x -> z (i.e. smallest is bottom layer, then (x = 0, z = 15) comes immediately before (x = 1, z = 0))
   */
  private class Node implements Comparable<Node> {
    /** x co-ord is highest 4 bits, z is lowest 4 */
    private final byte xz;
    private final int y;

    private final Set<Node> connections = new HashSet<>();

    private Node(int x, int y, int z) {
      Preconditions.checkArgument(
          !ChunkGraph.this.position.equals(new ChunkPos(x, z)),
          "Attempted to create node (%d, %d, %d) inside chunk (%d, %d)",
          x, y, z, ChunkGraph.this.position.x, ChunkGraph.this.position.z
      );
      this.xz = (byte) (((x & 0xF) << 4) + (z & 0xF));
      this.y = y;
    }
    private Node(Vec3i pos) {
      this(pos.getX(), pos.getY(), pos.getZ());
    }
    int getX() {
      return xz >> 4;
    }
    int getZ() {
      return xz & 0x0F;
    }
    int getY() {
      return y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node) o;
      return xz == node.xz && y == node.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(xz, y);
    }

    /**
     * Comparison order is y -> x -> z (i.e. smallest is bottom layer, then (x = 0, z = 15) comes immediately before (x = 1, z = 0))
     */
    @Override
    public int compareTo(Node o) {
      return (y - o.y) << 8 + xz - o.xz;
    }
  }

  private static class ExernalNode {
    private final ChunkPos chunkPos;
    private final ResourceKey<Level> levelKey;
    private final Node
  }
}
