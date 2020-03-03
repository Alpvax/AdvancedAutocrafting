package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
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
  public NonNullList<? extends INodeConnection<?>> getConnections() {
    return NonNullList.create();
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
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty(); //TODO: ???
  }
}
