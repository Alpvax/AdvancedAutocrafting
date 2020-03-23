package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.AdjacentNodeConnectionManager;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public interface INetworkNode {
  /*enum Connectivity {
    /**
     * Prevent connections on this side
     *
    BLOCK,
    /**
     * Attempt to connect on this face
     *
    CONNECT,
    /**
     * Neither attempt to connect or refuse connection.
     * Leave connectivity up to adjacent node
     *
    ACCEPT;
  }

  @Nonnull Connectivity getConnectivity(Direction dir);*/
  @Nonnull default AdjacentNodeConnectionManager createConnectionManager() {
    return new AdjacentNodeConnectionManager(this);
  }
  @Nonnull IWorldReader getWorld();
  @Nonnull BlockPos getPos();
  @Nonnull Set<NodeFunctionality<?>> getFunctionalities();
  @Nonnull <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality);
  @Nullable CraftNetwork getNetwork();
  default ITextComponent getName() {
    return getBlockState().getBlock().getNameTextComponent();
  }
  default int upkeepCost() {
    return 1;
  }
  default void onConnectionChange(Direction d, @Nullable INetworkNode oldNeighbor, @Nullable INetworkNode newNeighbor) {} //TODO: improve and use
  default void onNetworkConnect(CraftNetwork network) {}
  default void onNetworkDisconnect(CraftNetwork network) {}

  /* =========== Helper methods =========== */
  default void markDirty() {
    CraftNetwork network = getNetwork();
    if (network != null) {
      network.markDirty(this);
    }
  }
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

  /*static void handleNeighborChange(World worldIn, BlockPos pos, BlockPos fromPos) {
    BlockPos dPos = fromPos.subtract(pos);
    Direction d = Direction.byLong(dPos.getX(), dPos.getY(), dPos.getZ());
    getNodeAt(worldIn, pos, d.getOpposite()).ifPresent((thisNode) ->
        getNodeAt(worldIn, fromPos, d).filter(node -> node.getConnectivity(d.getOpposite()) != Connectivity.BLOCK).ifPresent((thatNode) -> {
          CraftNetwork thisNet = thisNode.getNetwork();
          CraftNetwork thatNet = thatNode.getNetwork();
          if (thisNet != thatNet) {
            if (thisNet != null) {
              thisNet.markDirty(thisNode);
              thisNet.markDirty(thatNode);
            }
            if (thatNet != null){
              thatNet.markDirty(thatNode);
              thatNet.markDirty(thisNode);
            }
          }
        })
    );
  }*/
  /*static Optional<INetworkNode> getAdjacentConnectedNode(BlockPos thisPos, IWorldReader world, Direction d) {
    Direction opp = d.getOpposite();
    return getNodeAt(world, thisPos.offset(d), opp).filter(node -> node.getConnectivity(opp) != Connectivity.BLOCK);
  }*/
}
