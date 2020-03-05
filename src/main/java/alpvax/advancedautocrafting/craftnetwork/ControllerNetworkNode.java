package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import alpvax.advancedautocrafting.craftnetwork.connection.DirectNodeConnection;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ControllerNetworkNode implements INetworkNode {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  private final ControllerTileEntity tileEntity;
  public ControllerNetworkNode(ControllerTileEntity tile) {
    tileEntity = tile;
  }

  @Nonnull
  @Override
  public NonNullList<INodeConnection<?>> getConnections() {
    NonNullList<INodeConnection<?>> list = NonNullList.create();
    for (Direction d : ALL_DIRECTIONS) {
      INetworkNode.getAdjacentNode(getPos(), tileEntity.getWorld(), d).ifPresent(node -> list.add(new DirectNodeConnection(this, node, d)));
    }
    return list;
  }

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

  @Override
  public void connectionChanged() {

  }

  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }

  @Override
  public int upkeepCost() {
    return 5;
  }
}
