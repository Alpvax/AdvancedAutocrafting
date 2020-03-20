package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.connection.AdjacentNodeConnectionManager;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class DummyNetworkNode implements INetworkNode {
  private final IWorldReader world;
  private final BlockPos pos;
  public DummyNetworkNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  /*@Nonnull
  @Override
  public Connectivity getConnectivity(Direction dir) {
    return Connectivity.ACCEPT;
  }*/

  @Nonnull
  @Override
  public DummyNodeConnectionManager createConnectionManager() {
    return new DummyNodeConnectionManager();
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
    return Collections.emptySet();
  }

  @Nonnull
  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }

  @Nullable
  @Override
  public CraftNetwork getNetwork() {
    return null;
  }

  @Override
  public int upkeepCost() {
    return 0;
  }

  private class DummyNodeConnectionManager extends AdjacentNodeConnectionManager{
    public DummyNodeConnectionManager() {
      super(DummyNetworkNode.this);
    }

    @Override
    public boolean canConnectFrom(Direction fromDir) {
      return false;
    }

    @Override
    public boolean canConnectTo(Direction dir) {
      return false;
    }
  }
}
