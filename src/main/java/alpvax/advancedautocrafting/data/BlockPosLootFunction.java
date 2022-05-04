package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;


public class BlockPosLootFunction extends LootItemConditionalFunction {
    private static final String NBT_KEY = AdvancedAutocrafting.MODID + ":position";

    public static LevelPosPair read(ItemStack stack) {
        return read(stack.getTag());
    }

    public static LevelPosPair read(@Nullable CompoundTag nbt) {
        if (nbt != null) {
            CompoundTag tag = nbt.getCompound(NBT_KEY);
            if (!tag.isEmpty()) {
                return new LevelPosPair(new ResourceLocation(tag.getString("level")), NbtUtils.readBlockPos(tag.getCompound("position")));
            }
        }
        return LevelPosPair.NONE;
    }

    public static void write(CompoundTag nbt, Level level, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("level", level.dimension().location().toString());
        tag.put("position", NbtUtils.writeBlockPos(pos));
        nbt.put(NBT_KEY, tag);
    }

    protected BlockPosLootFunction(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private static LootItemFunctionType TYPE;

    public static void register() {
        TYPE = Registry.register(
            Registry.LOOT_FUNCTION_TYPE,
            new ResourceLocation(AdvancedAutocrafting.MODID, "blockpos"),
            new LootItemFunctionType(new Serializer())
        );
    }

    public static LootItemFunction.Builder builder() {
        return simpleBuilder(BlockPosLootFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
        if (pos != null) {
            write(stack.getOrCreateTag(), context.getLevel(), new BlockPos(pos));
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<BlockPosLootFunction> {
        @Override
        public BlockPosLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            return new BlockPosLootFunction(conditionsIn);
        }
    }

    public static class LevelPosPair {
        private final ResourceLocation levelName;
        private final BlockPos pos;

        public LevelPosPair(ResourceLocation dimension, BlockPos pos) {
            this.levelName = dimension;
            this.pos = pos;
        }

        public boolean valid() {
            return true;
        }

        public BlockPos getPos() {
            return valid() ? pos : BlockPos.ZERO;
        }

        public ResourceLocation getLevelName() {
            return levelName;
        }

        public boolean matchesLevel(@Nullable Level level) {
            return valid() && level != null && levelName.equals(level.dimension().location());
        }

        @SuppressWarnings("ConstantConditions")
        private static final LevelPosPair NONE = new LevelPosPair(null, null) {
            @Override
            public boolean valid() {
                return false;
            }
        };
    }
}
