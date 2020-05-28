package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.util.BlockPosUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

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

  public static class Serializer extends LootFunction.Serializer<BlockPosLootFunction> {

    public Serializer() {
      super(new ResourceLocation(AdvancedAutocrafting.MODID, "blockpos"), BlockPosLootFunction.class);
    }

    @Override
    public BlockPosLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
      return new BlockPosLootFunction(conditionsIn);
    }
  }
}
