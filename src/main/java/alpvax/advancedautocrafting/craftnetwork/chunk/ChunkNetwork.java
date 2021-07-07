package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.segment.NetworkSegment;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

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

  protected Chunk getChunk() {
    return null;
  }

  public void addNode(@Nonnull BlockPos blockPos, @Nullable INetworkNode node) {
    Preconditions.checkArgument(new ChunkPos(blockPos).equals(pos), "Cannot add node @ %s to chunk %s (not the same chunk)", blockPos, pos);
    int x = blockPos.getX() & 15;
    int z = blockPos.getZ() & 15;
    if (x == 0) {
      sides.get(Direction.WEST);//TODO: .setNode(node)
    } else if (x == 15) {
      sides.get(Direction.EAST);//TODO: .setNode(node)
    }
    if (z == 0) {
      sides.get(Direction.NORTH);//TODO: .setNode(node)
    } else if (z == 15) {
      sides.get(Direction.SOUTH);//TODO: .setNode(node)
    }
    //TODO: Add node
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

    private BlockPos getInPlane(int axis1, int axis2) {
      switch (side.getAxis()) {
        case X:
          return new BlockPos(planePosition, axis1, axis2);
        case Y:
          return new BlockPos(axis1, planePosition, axis2);
        case Z:
          return new BlockPos(axis1, axis2, planePosition);
        default:
          throw new NullPointerException(side.getAxis().getName());
      }
    }

    /*public Optional<ChunkBorder> getBordering(BlockPos pos) {

      return level.getChunk()pos.relative(side);
    }*/
  }
}
