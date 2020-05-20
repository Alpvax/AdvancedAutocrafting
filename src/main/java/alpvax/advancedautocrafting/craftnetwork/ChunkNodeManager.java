package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkNodeManager implements INBTSerializable<CompoundNBT> {
  private final Chunk chunk;
  private final Map<BlockPos, INetworkNodeInstance> nodes = new HashMap<>();

  public ChunkNodeManager(Chunk chunk) {
    this.chunk = chunk;
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT nbt = new CompoundNBT();
    ListNBT list = new ListNBT();
    nodes.forEach((pos, node) -> {
      CompoundNBT tag = new CompoundNBT();
      NBTUtil.writeBlockPos(pos);
      tag.putUniqueId("nodeID", node.getNodeID());
      tag.putString("nodeType", node.getType().toString());
      if (node instanceof INBTSerializable<?>) {
        tag.put("additionalData", ((INBTSerializable<?>)node).serializeNBT());
      }
      list.add(tag);
    });
    nbt.put("nodes", list);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    nodes.clear();
    ListNBT list = nbt.getList("nodes", Constants.NBT.TAG_COMPOUND);
    list.forEach(t -> {
      CompoundNBT tag = (CompoundNBT)t;
      BlockPos p = NBTUtil.readBlockPos(tag);
      UUID id = tag.getUniqueId("nodeID");
      NodeType type = NodeType.get(tag.getString("nodeType"));
      INetworkNodeInstance node = type.create(id, new UniversalPos(chunk.getWorld(), p));
      if (node instanceof INBTSerializable<?>) {
        deserialiseAdditional(node, tag);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private <T extends INBT> void deserialiseAdditional(INetworkNodeInstance node, CompoundNBT tag) {
    ((INBTSerializable<T>)node).deserializeNBT((T)tag.get("additionalData"));
  }
}
