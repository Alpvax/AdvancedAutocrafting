package alpvax.advancedautocrafting.craftnetwork;

import com.google.common.base.Preconditions;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NetworkManager {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  private Map<BlockPos, NetworkNode> nodes =  new HashMap<>();
  private Map<BlockPos, NetworkBranch> branches = new HashMap<>();

  private void addNode(IWorldReader world, BlockPos pos) {
    NetworkNode node = new NetworkNode(world, pos);
    NetworkNode prev = nodes.put(pos, node);
    if (prev != null) {
      prev.onRemove();
    }
    node.onAdd();//TODO: Does nothing (no listeners registered yet)
  }

  private void updateAdjacent(BlockPos pos){
    for (Direction d : ALL_DIRECTIONS) {
      BlockPos aPos = pos.offset(d);
      //TODO:??
    }
  }


  private enum ConnectionResult { CONNECTED, DISCONNECTED, UNCHANGED}
  /**
   * Attempts to connect 2 adjacent nodes
   * @return true if the connection was established (or already existed), false otherwise.
   */
  private static ConnectionResult tryConnectNodes(NetworkNode a, NetworkNode b) {
    BlockPos diff = b.pos.subtract(a.pos);
    Preconditions.checkArgument(
        Math.abs(diff.getX() + diff.getY() + diff.getZ()) == 1,
        String.format("Attempted to connect to non adjacent nodes: %s %s", a.pos, b.pos)
    );
    Direction dA = Objects.requireNonNull(Direction.byLong(diff.getX(), diff.getY(), diff.getZ()));
    Direction dB = dA.getOpposite();
    boolean isA = a.isConnected(dA);
    boolean isB = b.isConnected(dB);
    if (isA && isB) {
      return ConnectionResult.UNCHANGED;
    } else {
      boolean canA = isA || a.canConnect(dA);
      boolean canB = isB || b.canConnect(dB);
      boolean connect = canA && canB; // Connect / Disconnect
      boolean changed = false;
      if (connect != isA) {
        a.connect(dA, connect);
        changed = true;
      }
      if (connect != isB) {
        b.connect(dB, connect);
        changed = true;
      }
      return changed
                 ? connect ? ConnectionResult.CONNECTED : ConnectionResult.DISCONNECTED
                 : ConnectionResult.UNCHANGED;
    }
  }
}
