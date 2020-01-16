package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public interface INetworkNode {
  @Nonnull NonNullList<INetworkNode> getChildNodes(Direction inbound);
  @Nonnull BlockPos getPos();
}
