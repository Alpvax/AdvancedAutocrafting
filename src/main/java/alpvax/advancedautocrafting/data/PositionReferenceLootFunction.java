package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.util.IPositionReference;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class PositionReferenceLootFunction extends LootItemConditionalFunction {
    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, AdvancedAutocrafting.MODID);

    protected PositionReferenceLootFunction(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private static final RegistryObject<LootItemFunctionType> TYPE = LOOT_FUNCTIONS.register("position_reference", () -> new LootItemFunctionType(new Serializer()));

    public static void register(IEventBus bus) {
        LOOT_FUNCTIONS.register(bus);
    }

    public static LootItemFunction.Builder builder() {
        return simpleBuilder(PositionReferenceLootFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
        if (pos != null) {
            IPositionReference.PositionMarkerItemStack.setPosition(stack, context.getLevel().dimension(), new BlockPos(pos));
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<PositionReferenceLootFunction> {
        @Override
        public PositionReferenceLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            return new PositionReferenceLootFunction(conditionsIn);
        }
    }
}
