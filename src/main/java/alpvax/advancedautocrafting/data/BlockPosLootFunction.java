package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockPosLootFunction extends LootFunction {
  private static final String NBT_KEY = AdvancedAutocrafting.MODID + ":position";

  @Nonnull
  public static WorldPosPair read(ItemStack stack) {
    return read(stack.getTag());
  }

  @Nonnull
  public static WorldPosPair read(CompoundNBT nbt) {
    if (nbt != null) {
      CompoundNBT tag = nbt.getCompound(NBT_KEY);
      if (!tag.isEmpty()) {
        return new WorldPosPair(new ResourceLocation(tag.getString("dimension")), NBTUtil.readBlockPos(tag.getCompound("position")));
      }
    }
    return WorldPosPair.NONE;
  }
  public static void write(CompoundNBT nbt, World world, BlockPos pos) {
    CompoundNBT tag = new CompoundNBT();
    tag.putString("dimension", world.func_234923_W_().func_240901_a_().toString());
    tag.put("position", NBTUtil.writeBlockPos(pos));
    nbt.put(NBT_KEY, tag);
  }

  protected BlockPosLootFunction(ILootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  private static LootFunctionType TYPE;
  public static void register() {
    TYPE = Registry.register(
        Registry.field_239694_aZ_,
        new ResourceLocation(AdvancedAutocrafting.MODID, "blockpos"),
        new LootFunctionType(new BlockPosLootFunction.Serializer())
    );
  }

  public static LootFunction.Builder<?> builder() {
    return builder(BlockPosLootFunction::new);
  }

  @Nonnull
  @Override
  protected ItemStack doApply(ItemStack stack, LootContext context) {
    BlockPos pos = context.get(LootParameters.POSITION);
    if (pos != null) {
      write(stack.getOrCreateTag(), context.getWorld(), pos);
    }
    return stack;
  }

  @Nonnull
  @Override
  public LootFunctionType func_230425_b_() {
    return TYPE;
  }

  public static class Serializer extends LootFunction.Serializer<BlockPosLootFunction> {
    @Nonnull
    @Override
    public BlockPosLootFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull ILootCondition[] conditionsIn) {
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
    public boolean matchesWorld(World world) {
      return valid() && dimension.equals(world.func_234923_W_().func_240901_a_());
    }
    private static final WorldPosPair NONE = new WorldPosPair(null, null) {
      @Override public boolean valid() { return false; }
    };
  }
}
