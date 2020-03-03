package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AAUtil {
  public static final String POSITION_NBT_KEY = AdvancedAutocrafting.MODID + ":position";

  public static void writePosToNBT(@Nonnull CompoundNBT nbt, @Nonnull BlockPos pos) {
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("x", pos.getX());
    tag.putInt("y", pos.getY());
    tag.putInt("z", pos.getZ());
    nbt.put(POSITION_NBT_KEY, tag);
  }

  @Nullable
  public static BlockPos readPosFromNBT(@Nonnull CompoundNBT nbt) {
    if(nbt.contains(POSITION_NBT_KEY,Constants.NBT.TAG_COMPOUND)) {
      CompoundNBT tag = nbt.getCompound(POSITION_NBT_KEY);
      return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
    return null;
  }

  public static boolean hasPosition(@Nonnull ItemStack stack) {
    return stack.getChildTag(POSITION_NBT_KEY) != null;
  }

  @Nullable
  public static BlockPos readPosFromItemStack(@Nonnull ItemStack stack) {
    CompoundNBT tag = stack.getChildTag(POSITION_NBT_KEY);
    return tag != null ? new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")) : null;
  }

  public static TranslationTextComponent getItemPositionText(@Nonnull ItemStack stack) {
    BlockPos pos = readPosFromItemStack(stack);
    return new TranslationTextComponent(AATranslationKeys.ITEM_POS_LORE, pos != null ? pos : "None");
  }

  public static Direction getDirection(BlockPos from, BlockPos to) {
    BlockPos vec = to.subtract(from);
    return Direction.getFacingFromVector(vec.getX(), vec.getY(), vec.getZ());
  }
}
