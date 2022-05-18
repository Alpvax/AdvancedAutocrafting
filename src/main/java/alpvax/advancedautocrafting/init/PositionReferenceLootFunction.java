package alpvax.advancedautocrafting.init;

import alpvax.advancedautocrafting.api.util.IPositionReference;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;


public class PositionReferenceLootFunction extends LootItemConditionalFunction {
    protected PositionReferenceLootFunction(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public static LootItemFunction.Builder builder() {
        return simpleBuilder(PositionReferenceLootFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
        if (pos != null) {
            IPositionReference.PositionMarkerItemStack.setPosition(
                stack, context.getLevel().dimension(), new BlockPos(pos));
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return AARegistration.POSITION_REFERENCE_LOOT.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<PositionReferenceLootFunction> {
        @Override
        public PositionReferenceLootFunction deserialize(
            JsonObject object,
            JsonDeserializationContext deserializationContext,
            LootItemCondition[] conditionsIn) {
            return new PositionReferenceLootFunction(conditionsIn);
        }
    }
}
