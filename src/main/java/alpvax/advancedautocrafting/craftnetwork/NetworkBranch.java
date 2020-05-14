package alpvax.advancedautocrafting.craftnetwork;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class NetworkBranch {
  class BranchNode extends NetworkNode {
    private BranchNode prev;
    private BranchNode next;
    public BranchNode(IWorldReader world, BlockPos pos) {
      super(world, pos);
    }
    private BranchNode(BranchNode toClone) {
      this(toClone.world, toClone.pos);
    }
    BranchNode linkNext(@Nonnull BranchNode node) {
      next = node;
      node.prev = this;
      return node;
    }
    BranchIterator iterForwards() {
      return new BranchIterator(this, false);
    }
    BranchIterator iterBackwards() {
      return new BranchIterator(this, true);
    }
  }

  private class BranchIterator implements Iterator<BranchNode> {
    private BranchNode nextNode;
    private BranchNode currentNode = null;
    private final boolean reverse;
    private BranchIterator(BranchNode start, boolean reverse) {
      nextNode = start;
      this.reverse = reverse;
    }
    @Override
    public boolean hasNext() {
      return nextNode != null;
    }
    @Override
    public BranchNode next() {
      currentNode = nextNode;
      nextNode = reverse ? currentNode.prev : currentNode.next;
      return currentNode;
    }
    public BlockPos currentPos() {
      return currentNode.pos;
    }
    public BlockPos nextPos() {
      return next().pos;
    }
  }

  private BranchNode start;
  private BranchNode end;
  private Map<BlockPos, BranchNode> nodeLookup = new HashMap<>();

  public NetworkBranch(IWorldReader world, Iterable<BlockPos> positions) {
    Iterator<BlockPos> i = positions.iterator();
    start = add(world, i.next());
    BranchNode node = start;
    while (i.hasNext()) {
      BlockPos pos = i.next();
      node = node.linkNext(add(world, i.next()));
    }
    end = node;
  }
  public NetworkBranch(BranchNode start) {
    this.start = add(start);
    BranchIterator i = start.iterForwards();
    BranchNode node = this.start;
    while (i.hasNext()) {
      node = node.linkNext(add(i.next()));
    }
    end = node;
  }
  private NetworkBranch(BranchIterator a, BranchIterator b) {
    start = add(a.next());
    BranchNode node = start;
    while (a.hasNext()) {
      node = node.linkNext(add(a.next()));
    }
    while (b.hasNext()) {
      node = node.linkNext(add(b.next()));
    }
    end = node;
  }

  private BranchNode add(IWorldReader world, BlockPos pos) {
    BranchNode node = new BranchNode(world, pos);
    nodeLookup.put(pos, node);
    return node;
  }
  private BranchNode add(BranchNode node) {
    node = new BranchNode(node);
    nodeLookup.put(node.pos, node);
    return node;
  }

  public BlockPos getStart() {
    return start.pos;
  }

  public BlockPos getEnd() {
    return end.pos;
  }

  BranchIterator iterForwards() {
    return start.iterForwards();
  }
  BranchIterator iterBackwards() {
    return start.iterBackwards();
  }

  public void extend(NetworkNode node) {
    boolean isStart = start.pos.manhattanDistance(node.pos) == 1;
    if (!isStart) {
      Preconditions.checkArgument(end.pos.manhattanDistance(node.pos) == 1, String.format(
          "Cannot join node which is not adjacent to one end of the branch!\nStart=%s; End=%s; adding: %s",
          start.pos, end.pos, node.pos
      ));
      end = end.linkNext(add(node.world, node.pos));
    } else {
      start = add(node.world, node.pos).linkNext(start);
    }
  }

  public Collection<NetworkBranch> split(BlockPos splitAt, @Nullable /*Junction*/NetworkNode junction) {
    Preconditions.checkArgument(nodeLookup.containsKey(splitAt), "Cannot split a branch on a position which is not in the branch");
    BranchNode node = nodeLookup.get(splitAt);
    BranchNode newEnd = new BranchNode(node.prev);
    BranchNode newStart = new BranchNode(node.next);
    node.prev = node.next = null;
    newEnd.next = null; //TODO: Connect to new junction?
    newStart.prev = null; //TODO: Connect to new junction?
    return Set.of(
        new NetworkBranch(start),
        new NetworkBranch(newStart)
    );
  }

  public static NetworkBranch join(NetworkBranch a, NetworkBranch b) {
    BranchIterator begin;
    BranchIterator end;
    if (a.start.equals(b.start)) {
      begin = a.iterBackwards();
      end = b.iterForwards();
    } else if (a.start.equals(b.end)) {
      begin = b.iterForwards();
      end = a.iterForwards();
    } else if (a.end.equals(b.start)) {
      begin = a.iterForwards();
      end = b.iterForwards();
    } else if (a.end.equals(b.end)) {
      begin = a.iterForwards();
      end = b.iterBackwards();
    } else {
      throw new IllegalArgumentException(String.format(
          "Attempted to join 2 branches whose ends do not intersect:\n(%s - %s) and (%s - %s)",
          a.start, a.end, b.start, b.end
      ));
    }
    return new NetworkBranch(begin, end);
  }
}
