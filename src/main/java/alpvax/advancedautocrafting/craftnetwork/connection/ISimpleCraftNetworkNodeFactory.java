package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

@FunctionalInterface
public interface ISimpleCraftNetworkNodeFactory {
  INetworkNode createNode(BlockState state, IWorldReader world, BlockPos pos);
}
