package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFuctionality;
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
  public NonNullList<INodeConnection> getConnections() {
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
  public <T> Optional<T> getFunctionality(NodeFuctionality<T> functionality) {
    return Optional.empty();
  }

  @Override
  public int upkeepCost() {
    return 0;
  }
}
