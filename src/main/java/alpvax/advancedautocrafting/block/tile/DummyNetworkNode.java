package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DummyNetworkNode implements INetworkNode {
  private final IWorldReader world;
  private final BlockPos pos;
  public DummyNetworkNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Nonnull
  @Override
  public NonNullList<INodeConnection<?>> getConnections() {
    return NonNullList.create();
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
