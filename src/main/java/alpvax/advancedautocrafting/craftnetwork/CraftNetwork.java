package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.connection.DirectNodeConnection;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftNetwork implements IEnergyStorage {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  private INetworkNode controller;
  private Set<INetworkNode> dirtyNodes = new HashSet<>();
  private Map<INetworkNode, Integer> nodeScores = new HashMap<>();
  private Multimap<NodeFunctionality<?>, INetworkNode> byFunction = HashMultimap.create();
  private int upkeep = 0;

  public CraftNetwork(INetworkNode controllerNode) {
    controller = controllerNode;
  }

  public ITextComponent chatNetworkDisplay() {
    ITextComponent text = new StringTextComponent("Network:\n");
    nodeScores.entrySet().stream()
        // Hide nodes which only provide connections
        .filter(e -> e.getKey().getFunctionalities().stream().anyMatch(nf -> nf != NodeFunctionality.EXTENDED_CONNECT))
        // Order by distance from controller
        .sorted(Comparator.comparingInt(Map.Entry::getValue))
        .forEachOrdered(e -> {
          INetworkNode node = e.getKey();
          BlockPos pos = node.getPos();
          text.appendSibling(node.getName())
              .appendText(String.format(" @ (%d, %d, %d); score = %d\n",
                  pos.getX(), pos.getY(), pos.getZ(),
                  e.getValue()
              ));
        });
    return text;
  }
  /*public void connect(INetworkNode node, ){}

  private int updateScore(INetworkNode node) {
    return -1;//TODO
  }*/

  public void markDirty(INetworkNode dirtyNode) {
    dirtyNodes.add(dirtyNode);
  }

  /*private void recalculateNode(INetworkNode node) {
    node
  }*/

  public void update() {
    if (!dirtyNodes.isEmpty()) {
      nodeScores = recalculateAll(); //TODO: update individual nodes
      upkeep = nodeScores.keySet().parallelStream().map(INetworkNode::upkeepCost).reduce(Integer::sum).orElse(0);
    }
    extractEnergy(upkeep, false);
  }

  Map<INetworkNode, Integer> recalculateAll() {
    return new NodeProcessor(controller).setProcessCallback((node, score) -> dirtyNodes.remove(node)).process();
  }

  public final boolean isActive() {
    return getEnergyStored() >= upkeep;
  }

  private Stream<IEnergyStorage> energyNodes() {
    return byFunction.get(NodeFunctionality.FORGE_ENERGY).stream()
               .map(node -> node.getFunctionality(NodeFunctionality.FORGE_ENERGY))
               .filter(Optional::isPresent)
               .map(Optional::get);
  }

  @Override
  public int receiveEnergy(final int maxReceive, boolean simulate) {
    Set<IEnergyStorage> energy = energyNodes().collect(Collectors.toSet());
    Iterator<IEnergyStorage> it = energy.iterator();
    int remaining = maxReceive;
    while (it.hasNext() && remaining > 0) {
      remaining -= it.next().receiveEnergy(remaining, simulate);
    }
    return maxReceive - remaining;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    Set<IEnergyStorage> energy = energyNodes().collect(Collectors.toSet());
    Iterator<IEnergyStorage> it = energy.iterator();
    int remaining = maxExtract;
    while (it.hasNext() && remaining > 0) {
      remaining -= it.next().extractEnergy(remaining, simulate);
    }
    return maxExtract - remaining;
  }

  @Override
  public int getEnergyStored() {
    return energyNodes()
               .map(IEnergyStorage::getEnergyStored)
               .reduce(Integer::sum).orElse(0);
  }

  @Override
  public int getMaxEnergyStored() {
    return energyNodes()
               .map(IEnergyStorage::getMaxEnergyStored)
               .reduce(Integer::sum).orElse(0);
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return energyNodes().anyMatch(IEnergyStorage::canReceive);
  }

  private static class NodeProcessor {
    private final Map<INetworkNode, Integer> processed = new HashMap<>();
    private final Map<INetworkNode, Integer> toProcess = new HashMap<>();
    private int currentScore = 0;
    private ObjIntConsumer<INetworkNode> callback = null;

    private NodeProcessor() {}
    private NodeProcessor(INetworkNode node) {
      this(node, 0);
    }
    private NodeProcessor(INetworkNode node, int score) {
      this();
      addNode(node, score);
    }
    public NodeProcessor setProcessCallback(ObjIntConsumer<INetworkNode> callback) {
      this.callback = callback;
      return this;
    }
    private void addNode(INetworkNode node, int score) {
      toProcess.compute(node, (k, prevScore) -> prevScore != null ? Math.min(prevScore, score) : score);
    }
    private Optional<NonNullList<INodeConnection<?>>> processNode(INetworkNode node) {
      int score = toProcess.compute(node, (k, prevScore) -> prevScore - 1); // Will throw NPE if node not in map
      if (score < 1) {
        NonNullList<INodeConnection<?>> list = NonNullList.create();
        for (Direction d : ALL_DIRECTIONS) {
          if (node.getConnectivity(d) == INetworkNode.Connectivity.CONNECT) {
            INetworkNode.getAdjacentConnectedNode(node.getPos(), node.getWorld(), d).ifPresent(adj -> {
              list.add(new DirectNodeConnection(node, adj, d));
            });
          }
        }
        node.getFunctionality(NodeFunctionality.EXTENDED_CONNECT).ifPresent(list::addAll);
        list.removeIf(conn -> processed.containsKey(conn.getChild()));
        toProcess.remove(node);
        return Optional.of(list);
      }
      return Optional.empty();
    }
    private void runLoop() {
      new HashMap<>(toProcess).forEach(((node, count) -> {
        Optional<NonNullList<INodeConnection<?>>> children = processNode(node);
        if (children.isPresent()) {
          processed.put(node, currentScore);
          if (callback != null) {
            callback.accept(node, currentScore);
          }
          children.get().forEach(connection -> addNode(connection.getChild(), connection.transferCost()));
        }
      }));
      currentScore++;
    }

    public Map<INetworkNode, Integer> process() {
      while (!toProcess.isEmpty()) {
        runLoop();
      }
      return processed;
    }
  }
}
