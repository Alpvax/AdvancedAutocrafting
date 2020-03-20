package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public /*abstract*/ class SimpleNetworkConnectionNode extends AbstractNetworkNode {
  public SimpleNetworkConnectionNode(Supplier<IWorldReader> worldSup, Supplier<BlockPos> posSup) {
    super(worldSup, posSup);
  }
  public SimpleNetworkConnectionNode(IWorldReader world, BlockPos pos) {
    super(world, pos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getWorld(), getPos());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SimpleNetworkConnectionNode) {
      SimpleNetworkConnectionNode node = (SimpleNetworkConnectionNode) obj;
      return getWorld().equals(node.getWorld()) && getPos().equals(node.getPos());
    }
    return super.equals(obj);
  }

  @Override
  public int upkeepCost() {
    return 0;
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
