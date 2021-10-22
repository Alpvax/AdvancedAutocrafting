package alpvax.advancedautocrafting.craftnetwork.impl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

interface INetworkPath {
  /**
   * @return the nodes at each end of the path. Should be sorted, but is not enforced
   */
  Pair<GraphNode, GraphNode> getEndpoints();

  /**
   * @return All the nodes which make up this path, including the endpoints
   */
  Set<GraphNode> includedNodes();

  /**
   * Check if this path is currently loaded.
   * @param check_all if true, check if all nodes are loaded, otherwise just check the endpoints
   * @return whether this path is in loaded chunks
   */
  default boolean isLoaded(boolean check_all) {
    if (check_all) {
      return includedNodes().stream().allMatch(GraphNode::isLoaded);
    }
    Pair<GraphNode, GraphNode> e = getEndpoints();
    return e.getLeft().isLoaded() && e.getRight().isLoaded();
  }

  /**
   * Whether this path is a spur, or part of a further chain
   * @return true if this path is only connected at one end
   */
  default boolean isSpur() {
    Pair<GraphNode, GraphNode> e = getEndpoints();
    return e.getLeft().isSpur() || e.getRight().isSpur();
  }

//TODO:  default void invalidatePath() {
//    includedNodes().stream().forEach();
//  }
}
