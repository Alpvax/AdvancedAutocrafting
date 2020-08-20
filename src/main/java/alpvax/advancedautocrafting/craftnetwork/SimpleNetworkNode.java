package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.container.util.ContainerBlockHolder;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class SimpleNetworkNode implements INetworkNode {
  private final ResourceLocation world;
  private final BlockPos pos;

  public SimpleNetworkNode(BlockPos pos, ResourceLocation worldID) {
    this.world = worldID;
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

  @Override
  public ContainerBlockHolder getProxy() {
    return new ContainerBlockHolder(pos, world);
  }
}
