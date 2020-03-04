package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.WireBlock;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public interface INetworkNode {
  @Nonnull NonNullList<? extends INodeConnection<?>> getConnections();
  @Nonnull BlockPos getPos();
  void connectionChanged();
  <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality);
  default int upkeepCost() {
    return 1;//TODO: Config?
  }



  static Optional<INetworkNode> getAdjacentNode(BlockPos thisPos, IWorldReader world, Direction d) {
    BlockPos target = thisPos.offset(d);
    BlockState state = world.getBlockState(target);
    if (state.hasTileEntity()) {
      LazyOptional<INetworkNode> cap = Objects.requireNonNull(world.getTileEntity(target)).getCapability(Capabilities.NODE_CAPABILITY, d.getOpposite());
      return Optional.ofNullable(cap.orElse(null));
    }
    if (state.getBlock() instanceof WireBlock) {
      IProperty<WireBlock.ConnectionState> prop = ((WireBlock)state.getBlock()).getConnectionProp(d.getOpposite());
      if(prop != null) {
        switch (state.get(prop)) {
          case CONNECTION:
          case INTERFACE:
            return Optional.of(new WireNetworkNode(world, target));
        }
      }
    }
    return Optional.empty();
  }
}
