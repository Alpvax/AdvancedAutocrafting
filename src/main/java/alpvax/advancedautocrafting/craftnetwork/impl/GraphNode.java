package alpvax.advancedautocrafting.craftnetwork.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class GraphNode {
  private final ResourceKey<Level> dimension;
  private final BlockPos position;
  private static final Map<ResourceLocation, Map<BlockPos, GraphNode>> instanceLookup = new HashMap<>();

  private GraphNode(ResourceKey<Level> dimension, BlockPos position) {
    this.dimension = dimension;
    this.position = position;
  }
  static GraphNode of(ResourceLocation dimensionID, BlockPos position) {
    return instanceLookup.computeIfAbsent(dimensionID, d -> new HashMap<>())
        .computeIfAbsent(position, p -> new GraphNode(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimensionID), position));
  }
  static GraphNode of(ResourceKey<Level> dimension, BlockPos position) {
    return instanceLookup.computeIfAbsent(dimension.location(), d -> new HashMap<>())
        .computeIfAbsent(position, p -> new GraphNode(dimension, position));
  }

  @Nullable
  ServerLevel getLevel(MinecraftServer server) {
    return server.getLevel(dimension);
  }

  private final Set<INodeConnector> connectors = new HashSet<>();
  private @Nullable INetworkPath onPath;

  Set<INodeConnector> getConnectors() {
    return connectors;
  }

  boolean isConnected() {
    return getConnectors().stream().anyMatch(INodeConnector::isConnected);
  }

  Stream<INetworkPath> getPaths() {
    if (onPath != null) {
      return Stream.of(onPath);
    }
    return connectors.stream().flatMap(c -> c.getPath().stream());
  }
  Stream<INetworkPath> getPathsExcept(INetworkPath... exceptions) {
    Set<INetworkPath> ex = Set.of(exceptions);
    return getPaths().filter(p -> !ex.contains(p));
  }

  /**
   * @return true if this node is only connected to 1 other node
   */
  boolean isSpur() {
    return getConnectors().stream().filter(INodeConnector::isConnected).count() == 1;
  }

  boolean isLoaded() {
    //TODO: Check chunk/dimension is loaded
    return false;
  }

  boolean isInterface() {
    //TODO: Check
    return false;
  }
  boolean isJunction() {
    return getConnectors().stream().filter(INodeConnector::isConnected).count() > 2;
  }
}
