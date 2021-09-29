package alpvax.advancedautocrafting.craftnetwork;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;

public interface INetworkNode {
  @Nonnull
  NonNullList<INetworkNode> getChildNodes(Direction inbound);
  @Nonnull
  BlockPos getPos();
}
