package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CraftNetwork implements INBTSerializable<CompoundNBT> {
  private Map<BlockPos, INetworkNode> nodes = new HashMap<>();
  private Map<ChunkPos, Integer> chunkCounts = new HashMap<>();
  final UUID id;

  CraftNetwork(UUID uuid) {
    id = uuid;
  }

  public void addNode(BlockPos pos, INetworkNode node) {
    if(!nodes.containsKey(pos)) {
      nodes.put(pos, node);
      ChunkPos cpos = new ChunkPos(pos);
      chunkCounts.put(cpos, chunkCounts.getOrDefault(cpos, 1));
    }
  }

  public void removeNode(BlockPos pos) {
    if(nodes.containsKey(pos)) {
      nodes.remove(pos);
      ChunkPos cpos = new ChunkPos(pos);
      int num = chunkCounts.get(cpos) - 1;
      if(num > 0) {
        chunkCounts.put(cpos, num);
      } else {
        chunkCounts.remove(cpos);
      }
    }
  }

  void invalidate() {
    //TODO: Update all nodes that this network is now removed (Should only happen when there are no more nodes in the network)
  }

  public boolean contains(BlockPos pos) {
    return nodes.containsKey(pos);
  }

  public int numNodes() {
    return nodes.size();
  }

  boolean inChunk(ChunkPos cpos) {
    return chunkCounts.containsKey(cpos);
  }

  @Override
  public CompoundNBT serializeNBT() {
    return new CompoundNBT();
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {

  }
}
