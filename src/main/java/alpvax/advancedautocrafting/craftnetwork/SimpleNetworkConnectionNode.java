package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class SimpleNetworkConnectionNode implements INetworkNode {
  private final IWorldReader world;
  private final BlockPos pos;

  public SimpleNetworkConnectionNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SimpleNetworkConnectionNode) {
      SimpleNetworkConnectionNode node = (SimpleNetworkConnectionNode) obj;
      return world.equals(node.world) && pos.equals(node.pos);
    }
    return super.equals(obj);
  }

  @Override
  public int upkeepCost() {
    return 0;
  }

  @Nonnull
  @Override
  public IWorldReader getWorld() {
    return world;
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Nonnull
  @Override
  public Set<NodeFunctionality<?>> getFunctionalities() {
    return Set.of();
  }

  @Nonnull
  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }
}
