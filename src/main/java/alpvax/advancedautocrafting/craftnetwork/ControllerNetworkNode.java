package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class ControllerNetworkNode implements INetworkNode {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  private final ControllerTileEntity tileEntity;
  public ControllerNetworkNode(ControllerTileEntity tile) {
    tileEntity = tile;
  }

  /*@Nonnull
  @Override
  public Connectivity getConnectivity(Direction dir) {
    return Connectivity.CONNECT;
  }*/

  @Nonnull
  @Override
  public IWorldReader getWorld() {
    return tileEntity.getWorld();
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return tileEntity.getPos();
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

  @Nullable
  @Override
  public CraftNetwork getNetwork() {
    return tileEntity.getNetwork();
  }

  @Override
  public int upkeepCost() {
    return 5;
  }
}
