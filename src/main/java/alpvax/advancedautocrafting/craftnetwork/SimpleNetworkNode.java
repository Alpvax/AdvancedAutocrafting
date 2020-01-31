package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SimpleNetworkNode implements INetworkNode {
  private final World world;
  private final BlockPos pos;

  public SimpleNetworkNode(BlockPos pos, World world) {
    this.world = world;
    this.pos = pos;
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Nonnull
  @Override
  public World getWorld() {
    return this.world;
  }

  @Nonnull
  @Override
  public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
    return NonNullList.create();
  }
}
