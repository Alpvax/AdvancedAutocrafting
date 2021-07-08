package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.segment.NetworkSegment;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkNetwork implements INetworkSegmentProvider {
  private final Set<NetworkSegment> segments = new HashSet<>();
  private final Map<Direction, ChunkBorder> sides = Maps.newEnumMap(Direction.class);
  private final ChunkPos pos;

  public ChunkNetwork(ChunkPos pos) {
    this.pos = pos;
    Direction.Plane.HORIZONTAL.stream().forEach(d -> sides.put(d, new ChunkBorder(d, pos)));
  }

  public void addNode(@Nonnull BlockPos blockPos, @Nullable INetworkNode node) {
    updateSides(blockPos, true);
    NetworkSegment.addNode(segments, blockPos, node);
  }

  private void updateSides(@Nonnull BlockPos blockPos, boolean existing) {
    Preconditions.checkArgument(new ChunkPos(blockPos).equals(pos), "Cannot add node @ %s to chunk %s (not the same chunk)", blockPos, pos);
    int x = blockPos.getX() & 15;
    int z = blockPos.getZ() & 15;
    if (x == 0) {
      sides.get(Direction.WEST).setNode(blockPos, existing);
    } else if (x == 15) {
      sides.get(Direction.EAST).setNode(blockPos, existing);
    }
    if (z == 0) {
      sides.get(Direction.NORTH).setNode(blockPos, existing);
    } else if (z == 15) {
      sides.get(Direction.SOUTH).setNode(blockPos, existing);
    }
  }

  @Override
  public Set<NetworkSegment> availableSegments() {
    return segments;
  }

  protected Set<NetworkSegment> segmentsForSide(Direction d) {
    if (sides.containsKey(d)) {
      Set<BlockPos> positions = sides.get(d).nodePositions;
      return segments.stream().filter(seg -> positions.stream().anyMatch(seg::contains)).collect(Collectors.toSet());
    }
    return availableSegments();
  }

  /**
   * Edge of a chunk in the horizontal plane. (One for each cardinal dirction).
   */
  private static class ChunkBorder {
    private final Direction side;
    private final int planePosition;

    private final Set<BlockPos> nodePositions = new HashSet<>();

    ChunkBorder(Direction side, ChunkPos pos) {
      this.side = side;
      planePosition = side.getAxis().choose(pos.x, 0, pos.z);
    }

    private boolean isInPlane(BlockPos pos) {
      return planePosition == side.getAxis().choose(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Convert 2d position into BlockPos
     * @param column x-coordinate for North & South, z-coordinate for East & West
     * @param height height of the blockpos
     * @return a blockpos which is guaranteed to be in the plane
     */
    private BlockPos getInPlane(int column, int height) {
      switch (side.getAxis()) {
        case X: // Plane YZ
          return new BlockPos(planePosition, height, column);
        /* Horizontal planes not supported
        case Y: // Plane XZ
          return new BlockPos(axis1, planePosition, axis2);*/
        case Z: // Plane XY
          return new BlockPos(column, height, planePosition);
        default:
          throw new NullPointerException(side.getAxis().getName());
      }
    }

    public boolean setNode(BlockPos pos, boolean existing) {
      return (existing && nodePositions.add(pos)) || nodePositions.remove(pos);
    }

    /*public Optional<ChunkBorder> getBordering(BlockPos pos) {

      return level.getChunk()pos.relative(side);
    }*/
  }
}
