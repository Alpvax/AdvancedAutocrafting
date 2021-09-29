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

import javax.annotation.Nonnull;

public class BlockPosLootFunction extends LootItemConditionalFunction {
  private static final String NBT_KEY = AdvancedAutocrafting.MODID + ":position";

  @Nonnull
  public static WorldPosPair read(ItemStack stack) {
    return read(stack.getTag());
  }

  @Nonnull
  public static WorldPosPair read(CompoundTag nbt) {
    if (nbt != null) {
      CompoundTag tag = nbt.getCompound(NBT_KEY);
      if (!tag.isEmpty()) {
        return new WorldPosPair(new ResourceLocation(tag.getString("dimension")), NbtUtils.readBlockPos(tag.getCompound("position")));
      }
    }
    return WorldPosPair.NONE;
  }
  public static void write(CompoundTag nbt, Level level, BlockPos pos) {
    CompoundTag tag = new CompoundTag();
    tag.putString("dimension", level.dimension().location().toString());
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

  @Nonnull
  @Override
  protected ItemStack run(@Nonnull ItemStack stack, LootContext context) {
    Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
    if (pos != null) {
      write(stack.getOrCreateTag(), context.getLevel(), new BlockPos(pos));
    }
    return stack;
  }

  @Nonnull
  @Override
  public LootItemFunctionType getType() {
    return TYPE;
  }

  public static class Serializer extends LootItemConditionalFunction.Serializer<BlockPosLootFunction> {
    @Nonnull
    @Override
    public BlockPosLootFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootItemCondition[] conditionsIn) {
      return new BlockPosLootFunction(conditionsIn);
    }
  }

  public static class WorldPosPair {
    private final ResourceLocation dimension;
    private final BlockPos pos;
    public WorldPosPair(ResourceLocation dimension, BlockPos pos) {
      this.dimension = dimension;
      this.pos = pos;
    }
    public boolean valid() {
      return true;
    }
    public BlockPos getPos() {
      return valid() ? pos : BlockPos.ZERO;
    }
    public ResourceLocation getWorldID() {
      return valid() ? dimension : null;
    }
    public boolean matchesLevel(Level level) {
      return valid() && dimension.equals(level.dimension().location());
    }
    private static final WorldPosPair NONE = new WorldPosPair(null, null) {
      @Override public boolean valid() { return false; }
    };
  }
}
