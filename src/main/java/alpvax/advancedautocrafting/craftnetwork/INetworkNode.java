package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFuctionality;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface INetworkNode {
  @Nonnull NonNullList<INodeConnection> getConnections();
  @Nonnull BlockPos getPos();
  void connectionChanged();
  <T> Optional<T> getFunctionality(NodeFuctionality<T> functionality);
  default int upkeepCost() {
    return 1;//TODO: Config?
  }
}
