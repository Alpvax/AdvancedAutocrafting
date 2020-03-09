package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.connection.ISimpleCraftNetworkNodeFactory;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface INetworkNode {
  enum Connectivity {
    /**
     * Prevent connections on this side
     */
    BLOCK,
    /**
     * Attempt to connect on this face
     */
    CONNECT,
    /**
     * Neither attempt to connect or refuse connection.
     * Leave connectivity up to adjacent node
     */
    ACCEPT;
  }
  @Nonnull Connectivity getConnectivity(Direction dir);
  @Nonnull IWorldReader getWorld();
  @Nonnull BlockPos getPos();
  @Nonnull Set<NodeFunctionality<?>> getFunctionalities();
  @Nonnull <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality);
  default ITextComponent getName() {
    return getBlockState().getBlock().getNameTextComponent();
  }
  default int upkeepCost() {
    return 1;
  }

  /* =========== Helper methods =========== */
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
    if (tileEntityClass.isInstance(tile)) {
      return tileEntityClass.cast(tile);
    }
    return null;
  }

  Map<Block, ISimpleCraftNetworkNodeFactory> NON_TILE_NODES = new HashMap<>();

  static Optional<INetworkNode> getNodeAt(IWorldReader world, BlockPos pos, @Nullable Direction fromDir) {
    BlockState state = world.getBlockState(pos);
    TileEntity tile = world.getTileEntity(pos);
    if (tile != null) {
      LazyOptional<INetworkNode> cap = tile.getCapability(Capabilities.NODE_CAPABILITY, fromDir != null ? fromDir.getOpposite() : null);
      if (cap.isPresent()) {
        return  Optional.of(cap.orElseThrow(() -> new NullPointerException("Impossible condition reached: null cap passes isPresent check")));
      }
    }
    Block block = state.getBlock();
    INetworkNode node = null;
    if (NON_TILE_NODES.containsKey(block)) {
      node = NON_TILE_NODES.get(block).createNode(state, world, pos);
    } else if (state.getBlock() instanceof ISimpleCraftNetworkNodeFactory) {
      node = ((ISimpleCraftNetworkNodeFactory) block).createNode(state, world, pos);
    }
    return Optional.ofNullable(node);
  }

  static Optional<INetworkNode> getAdjacentConnectedNode(BlockPos thisPos, IWorldReader world, Direction d) {
    Direction opp = d.getOpposite();
    return getNodeAt(world, thisPos.offset(d), opp).filter(node -> node.getConnectivity(opp) != Connectivity.BLOCK);
  }
}
