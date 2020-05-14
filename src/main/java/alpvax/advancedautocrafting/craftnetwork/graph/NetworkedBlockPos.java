package alpvax.advancedautocrafting.craftnetwork.graph;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

class NetworkedBlockPos {
  protected static final Direction[] ALL_DIRECTIONS = Direction.values();

  public final BlockPos pos;
  //TODO: private final EnumMap<Direction, NetworkEdge> edges?

  NetworkedBlockPos(BlockPos pos) {
    this.pos = pos;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof NetworkedBlockPos) {
      NetworkedBlockPos node = (NetworkedBlockPos)obj;
      return pos.equals(node.pos);
    }
    return false;
  }
}
