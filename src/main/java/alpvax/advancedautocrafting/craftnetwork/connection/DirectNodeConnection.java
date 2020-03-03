package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class DirectNodeConnection implements INodeConnection<DirectNodeConnection> {
  private final INetworkNode parent;
  private final INetworkNode child;
  private final Direction direction;

  public DirectNodeConnection(INetworkNode parent, INetworkNode child, Direction direction) {
    this.parent = parent;
    this.child = child;
    this.direction = direction;
  }

  public DirectNodeConnection(INetworkNode parent, INetworkNode child) {
    this(parent, child, getDirection(parent.getPos(), child.getPos()));
  }

  private static Direction getDirection(BlockPos from, BlockPos to) {
    BlockPos vec = to.subtract(from);
    return Direction.getFacingFromVector(vec.getX(), vec.getY(), vec.getZ());
  }

  @Override
  public int transferCost() {
    return 1;
  }

  public Direction childDirection() {
    return direction;
  }

  @Override
  public INetworkNode getParent() {
    return parent;
  }

  @Override
  public INetworkNode getChild() {
    return child;
  }

  @Override
  public DirectNodeConnection invert() {
    return new DirectNodeConnection(child, parent, direction.getOpposite());
  }
}
