package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class SimpleNetworkNode implements INetworkNode {
  private final BlockPos pos;

  public SimpleNetworkNode(BlockPos pos) {
    this.pos = pos;
  }

  @Nonnull
  @Override
  public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
    return NonNullList.create();
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }
}
