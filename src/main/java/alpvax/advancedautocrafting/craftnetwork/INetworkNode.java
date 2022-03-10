package alpvax.advancedautocrafting.craftnetwork;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;


public interface INetworkNode {
  NonNullList<INetworkNode> getChildNodes(Direction inbound); //TODO: remove
  BlockPos getPos();
}
