package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFuctionality;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SimpleNetworkNode implements INetworkNode {
  private final BlockPos pos;

  public SimpleNetworkNode(BlockPos pos) {
    this.pos = pos;
  }

  @Nonnull
  @Override
  public NonNullList<INodeConnection> getConnections() {
    return null;
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public void connectionChanged() {
    //TODO: what happens here?
  }

  @Override
  public <T> Optional<T> getFunctionality(NodeFuctionality<T> functionality) {
    return Optional.empty(); //TODO: ???
  }
}
