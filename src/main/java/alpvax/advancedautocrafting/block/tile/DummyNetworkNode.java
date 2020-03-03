package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DummyNetworkNode implements INetworkNode {
  private final BlockPos pos;
  public DummyNetworkNode(BlockPos pos) {
    this.pos = pos;
  }

  @Nonnull
  @Override
  public NonNullList<? extends INodeConnection<?>> getConnections() {
    return NonNullList.create();
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public void connectionChanged() {}

  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }

  @Override
  public int upkeepCost() {
    return 0;
  }
}
