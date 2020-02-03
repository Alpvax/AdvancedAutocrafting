package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface INetworkManager extends INBTSerializable<CompoundNBT> {

  void addNode(BlockPos pos, @Nullable INetworkNode node);
  void removeNode(BlockPos pos);
}
