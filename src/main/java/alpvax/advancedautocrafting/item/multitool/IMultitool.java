package alpvax.advancedautocrafting.item.multitool;

import alpvax.advancedautocrafting.Capabilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public interface IMultitool extends INBTSerializable<CompoundNBT> {
  //ItemStack selectTool(BlockState lookedAt, IWorldReader world, BlockPos pos);
  @Nullable ItemStack selectToolForBlock(LivingEntity user, BlockState lookedAt, IWorldReader world, BlockPos pos);
  @Nullable ItemStack selectToolForEntity(LivingEntity user, MultiToolType type, Entity target);
  @Nullable ItemStack selectToolForType(MultiToolType type);
  @Nullable ItemStack selectWeapon(LivingEntity user, LivingEntity target);
  Stream<ItemStack> allTools();

   class Provider implements ICapabilitySerializable<CompoundNBT> {
    private IMultitool multitool;

    public Provider() {
      this(new MultitoolInst());
    }
    public Provider(IMultitool multitool) {
      this.multitool = multitool;
    }

    private LazyOptional<IMultitool> capability = LazyOptional.of(() -> multitool);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == Capabilities.MULTITOOL_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

     @Override
     public CompoundNBT serializeNBT() {
       return multitool.serializeNBT();
     }

     @Override
     public void deserializeNBT(CompoundNBT nbt) {
      multitool.deserializeNBT(nbt);
     }
   }

  interface Modifiable extends IMultitool {
     boolean canAddTool(ItemStack tool);
     boolean addTool(ItemStack tool);
  }
}
