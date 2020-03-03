package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface INetworkNode {
  @Nonnull NonNullList<? extends INodeConnection<?>> getConnections();
  @Nonnull BlockPos getPos();
  void connectionChanged();
  <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality);
  default int upkeepCost() {
    return 1;//TODO: Config?
  }
}
