package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class SimpleNetworkNode implements INetworkNode {
  private final BlockPos pos;

  public SimpleNetworkNode(BlockPos pos) {
    this.pos = pos;
  }

  @Override
  public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
    return NonNullList.create();
  }

  @Override
  public BlockPos getPos() {
    return pos;
  }
}
