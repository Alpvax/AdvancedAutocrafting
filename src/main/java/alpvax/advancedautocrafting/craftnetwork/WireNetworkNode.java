package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.block.WireBlock;
import alpvax.advancedautocrafting.craftnetwork.connection.DirectNodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.BlockState;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.Optional;

public class WireNetworkNode implements INetworkNode {
  private final IWorldReader world;
  private final BlockPos pos;

  public WireNetworkNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Override
  public int upkeepCost() {
    return 0;
  }

  @Nonnull
  @Override
  public NonNullList<DirectNodeConnection> getConnections() {
    NonNullList<DirectNodeConnection> list = NonNullList.create();
    BlockState state = world.getBlockState(getPos());
    if (state.getBlock() instanceof WireBlock) {
      ((WireBlock)state.getBlock()).forEachDirection((d, p) -> {
        switch (state.get(p)) {
          case INTERFACE:
            INetworkNode.getAdjacentNode(getPos(), world, d).ifPresent(node -> list.add(new DirectNodeConnection(this, node, d)));
            break;
          case CONNECTION:
            list.add(new DirectNodeConnection(this, new WireNetworkNode(world, getPos().offset(d))));
            break;
        }
      });
    }
    return list;
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public void connectionChanged() {

  }

  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }
}
