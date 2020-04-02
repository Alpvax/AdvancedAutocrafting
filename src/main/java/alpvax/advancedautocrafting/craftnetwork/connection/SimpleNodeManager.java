package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.CraftNetwork;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import com.google.common.base.Preconditions;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SimpleNodeManager {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final Direction[] ALL_DIRECTIONS = Direction.values();
  private static final Map<IWorldReader, SimpleNodeManager> managers = new HashMap<>();

  private static SimpleNodeManager forWorld(IWorldReader world) {
    return managers.computeIfAbsent(world, SimpleNodeManager::new);
  }

  private final IWorldReader world;
  private final Map<BlockPos, NetworkMapping> nodes = new HashMap<>();
  private SimpleNodeManager(IWorldReader world) {
    this.world = world;
  }

  public INetworkNode getNode(BlockPos pos) {
    NetworkMapping n = nodes.get(pos);
    return n == null ? null : n.node;
  }
  public CraftNetwork getNetwork(BlockPos pos) {
    NetworkMapping n = nodes.get(pos);
    return n == null ? null : n.network;
  }
  /*public NetworkMapping addNode(INetworkNode node) {
    BlockPos pos = node.getPos();
    CraftNetwork network = null;
    for (Direction d : ALL_DIRECTIONS) {
      if (node.getConnectivity(d) == INetworkNode.Connectivity.CONNECT) {
        Optional<INetworkNode> adj = INetworkNode.getAdjacentConnectedNode(node.getPos(), node.getWorld(), d);
        if (adj.isPresent()) {
          CraftNetwork anet = adj.get().getNetwork();
          if (network != anet) {
            if (network != null) {
              LOGGER.error("Simple Node {} being added which could be a member of multiple networks!\n {} {}", node, network, adj);
            }
            network = anet;
          }
        }
      }
    }
    return setNode(pos, node, network);
  }*/
  NetworkMapping setNode(BlockPos pos, INetworkNode node) {
    return setNode(pos, node, null);
  }
  NetworkMapping setNode(BlockPos pos, INetworkNode node, CraftNetwork network) {
    Preconditions.checkArgument(node.getWorld() == world, "Tried to add node %s with world %s to world %s", node, node.getWorld(), world);
    NetworkMapping n = new NetworkMapping(node, network);
    nodes.put(pos, n);
    return n;
  }

  static class NetworkMapping {
    private final INetworkNode node;
    private CraftNetwork network = null;

    private NetworkMapping(INetworkNode node) {
      this.node = node;
    }
    private NetworkMapping(INetworkNode node, CraftNetwork network) {
      this(node);
      setNetwork(network);
    }
    public NetworkMapping setNetwork(CraftNetwork network) {
      this.network = network;
      return this;
    }
  }
}
