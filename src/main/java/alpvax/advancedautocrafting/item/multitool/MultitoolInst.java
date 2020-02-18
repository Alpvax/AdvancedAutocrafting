package alpvax.advancedautocrafting.item.multitool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class MultitoolInst implements IMultitool.Modifiable {
  private Set<ItemStack> tools;
  public MultitoolInst() {
    tools = new HashSet<>();
  }
  public static MultitoolInst from(CompoundNBT nbt) {
    MultitoolInst inst = new MultitoolInst();
    if(nbt != null) {
      inst.deserializeNBT(nbt);
    }
    return inst;
  }



  /*@Override
  public ItemStack selectTool(BlockState lookedAt, IWorldReader world, BlockPos pos) {
    return null;
  }*/

  @Nullable
  @Override
  public ItemStack selectToolForBlock(LivingEntity user, BlockState lookedAt, IWorldReader world, BlockPos pos) {
    return null;//TODO:
  }

  @Nullable
  @Override
  public ItemStack selectToolForEntity(LivingEntity user, Entity target) {
    return null;//TODO:
  }

  @Nullable
  @Override
  public ItemStack selectToolForType(MultiToolType type) {
    return null;//TODO:
  }

  @Nullable
  @Override
  public ItemStack selectWeapon(LivingEntity user, LivingEntity target) {
    return null;//TODO:
  }

  @Override
  public Stream<ItemStack> allTools() {
    return tools.stream();
  }

  @Override
  public boolean canAddTool(ItemStack tool) {
    if(tool.isEmpty()) {
      return false;
    }
    return tool.getToolTypes().size() > 0; //TODO: Other types?
  }

  @Override
  public boolean addTool(ItemStack tool) {
    return canAddTool(tool) && tools.add(tool.copy());
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT nbt = new CompoundNBT();
    ListNBT list = new ListNBT();
    for(ItemStack tool : tools) {
      list.add(tool.write(new CompoundNBT()));
    }
    nbt.put("tools", list);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    tools.clear();
    ListNBT list = nbt.getList("tools", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i < list.size(); i++) {
      tools.add(ItemStack.read(list.getCompound(i)));
    }
  }
}
