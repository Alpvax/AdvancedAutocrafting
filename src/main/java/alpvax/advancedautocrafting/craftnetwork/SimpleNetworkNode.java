package alpvax.advancedautocrafting.craftnetwork;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;

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
