package alpvax.advancedautocrafting.craftnetwork.graph;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.node.INetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NetworkGraph {
  private Map<BlockPos, NetworkedBlockPos> loadedPositions =  new HashMap<>();
  private Map<BlockPos, INetworkNode> nodes =  new HashMap<>();

  public Optional<INetworkNode> getNode(BlockPos pos) {
    return Optional.ofNullable(nodes.get(pos)); //TODO: fix implementation
  }

  /**
   * Will be called for each neighbour of neighbourPos, when neighbour changes.
   * Use to recalculate connections.
   * @param pos the pos of the block to notify
   * @param neighbourPos the pos of the changed block
   * @param neighbourDirection the direction of the neighbour (from pos)
   * @param neighbourState the blockstate of the neighbour
   */
  private void onNeighbourUpdate(BlockPos pos, BlockPos neighbourPos, Direction neighbourDirection, BlockState neighbourState) {
    NetworkedBlockPos node = loadedPositions.get(pos);
    if (node != null) {
      //TODO: update node connections
    }
  }

  public static void onNeighbourUpdate(BlockEvent.NeighborNotifyEvent event) {
    IWorld world = event.getWorld();
    if (world instanceof ICapabilityProvider) {
      LazyOptional<NetworkGraph> cap = ((ICapabilityProvider)world).getCapability(Capabilities.NETWORK_GRAPH_CAPABILITY);
      cap.ifPresent(graph -> {
        BlockPos pos = event.getPos();
        BlockState newState = event.getState();
        event.getNotifiedSides().forEach(d -> {
          graph.onNeighbourUpdate(pos.offset(d), pos, d.getOpposite(), newState);
        });
      });
    }
  }
}
