package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.WireBlock;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public interface INetworkNode {
  @Nonnull
  NonNullList<INodeConnection<?>> getConnections();
  @Nonnull IWorldReader getWorld();
  @Nonnull BlockPos getPos();
  void connectionChanged();
  <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality);
  @Nonnull
  default BlockState getBlockState() {
    return getWorld().getBlockState(getPos());
  }
  @Nonnull
  default IFluidState getFluidState() {
    return getWorld().getFluidState(getPos());
  }
  @Nullable
  default TileEntity getTileEntity() {
    return getWorld().getTileEntity(getPos());
  }
  @Nullable
  default <T extends TileEntity> T getTileEntity(Class<T> tileEntityClass) {
    TileEntity tile = getTileEntity();
    if (tile != null && tileEntityClass.isInstance(tile)) {
      return tileEntityClass.cast(tile);
    }
    return null;
  }
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
