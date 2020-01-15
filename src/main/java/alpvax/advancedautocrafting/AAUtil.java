package alpvax.advancedautocrafting;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class AAUtil {
  public static void writePosToNBT(CompoundNBT nbt, BlockPos pos) {
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("x", pos.getX());
    tag.putInt("y", pos.getY());
    tag.putInt("z", pos.getZ());
    nbt.put(AdvancedAutocrafting.MODID + ":position", tag);
  }

  public static BlockPos readPosFromNBT(CompoundNBT nbt) {
    CompoundNBT tag = nbt.getCompound(AdvancedAutocrafting.MODID + ":position");
    return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
  }
}
