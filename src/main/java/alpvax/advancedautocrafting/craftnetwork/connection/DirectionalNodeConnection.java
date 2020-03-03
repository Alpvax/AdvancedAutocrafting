package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class DirectionalNodeConnection implements INodeConnection<DirectionalNodeConnection> {
  private final INetworkNode parent;
  private final INetworkNode child;
  private final Direction direction;
  private final int distance;

  public DirectionalNodeConnection(INetworkNode parent, INetworkNode child, Direction direction) {
    this.parent = parent;
    this.child = child;
    this.direction = direction;
    Direction.Axis axis = direction.getAxis();
    BlockPos ppos = parent.getPos();
    BlockPos cpos = child.getPos();
    distance = axis.getCoordinate(ppos.getX(), ppos.getY(), ppos.getZ())
             - axis.getCoordinate(cpos.getX(), cpos.getY(), cpos.getZ());
  }

  public DirectionalNodeConnection(INetworkNode parent, INetworkNode child) {
    this(parent, child, AAUtil.getDirection(parent.getPos(), child.getPos()));
  }

  @Override
  public int transferCost() {
    //TODO: increase cost for non-line-of-sight
    return distance * 2;
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
  public DirectionalNodeConnection invert() {
    return new DirectionalNodeConnection(child, parent, direction.getOpposite());
  }
}
