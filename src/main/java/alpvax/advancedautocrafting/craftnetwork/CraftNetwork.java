package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.function.NodeFuctionality;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftNetwork implements IEnergyStorage {
  private INetworkNode controller;
  private Set<INetworkNode> dirtyNodes = new HashSet<>();
  private Object2IntMap<INetworkNode> nodeScores = Object2IntMaps.emptyMap();
  private Multimap<NodeFuctionality<?>, INetworkNode> byFunction = HashMultimap.create();

  /*public void connect(INetworkNode node, ){}

  private int updateScore(INetworkNode node) {
    return -1;//TODO
  }*/

  void markDirty(INetworkNode dirtyNode) {
    dirtyNodes.add(dirtyNode);
  }

  /*private void recalculateNode(INetworkNode node) {
    node
  }*/

  public void update() {
    if (!dirtyNodes.isEmpty()) {
      nodeScores = recalculateAll(); //TODO: update individual nodes
    }
    nodeScores.keySet().forEach(node -> extractEnergy(node.upkeepCost(), true));
  }

  Object2IntMap<INetworkNode> recalculateAll() {
    Object2IntMap<INetworkNode> visited = Object2IntMaps.emptyMap();
    Multimap<Integer, INetworkNode> toVisit = HashMultimap.create();
    int currentScore = 0;
    //Set<INetworkNode> nodes = new HashSet<>();
    toVisit.put(0, controller);
    while (!toVisit.isEmpty()) {
      int cScore = currentScore;
      Set<Pair<Integer, INetworkNode>> nodes = toVisit.get(currentScore).stream().flatMap(node -> {
        visited.put(node, cScore);
        return node.getConnections().stream().map(conn -> Pair.of(cScore + conn.transferCost(), conn.getChild()));
      }).collect(Collectors.toSet());
      toVisit.removeAll(currentScore);
      currentScore++;
      nodes.forEach(nodePair -> toVisit.put(nodePair.getKey(), nodePair.getValue()));
    }
    return visited;
  }

  private Stream<IEnergyStorage> energyNodes() {
    return byFunction.get(NodeFuctionality.ENERGY).stream()
               .map(node -> node.getFunctionality(NodeFuctionality.ENERGY))
               .filter(Optional::isPresent)
               .map(o -> o.get());
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
    return 0;
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
}
