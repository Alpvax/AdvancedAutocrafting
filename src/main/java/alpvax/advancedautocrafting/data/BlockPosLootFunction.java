package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.util.BlockPosUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class BlockPosLootFunction extends LootFunction {
  protected BlockPosLootFunction(ILootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  public static LootFunction.Builder<?> builder() {
    return builder((conditions) -> new BlockPosLootFunction(conditions));
  }

  @Override
  protected ItemStack doApply(ItemStack stack, LootContext context) {
    BlockPos pos = context.get(LootParameters.POSITION);
    CompoundNBT stackNBT = stack.getOrCreateTag();
    BlockPosUtil.writePosToNBT(stackNBT, pos);
    return stack;
  }

  @Override
  public LootFunctionType func_230425_b_() {
    return null;
  }

  public static class Serializer extends LootFunction.Serializer<BlockPosLootFunction> {
    @Override
    public BlockPosLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
      return new BlockPosLootFunction(conditionsIn);
    }
  }
}
