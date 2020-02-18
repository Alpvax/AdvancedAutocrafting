package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.item.multitool.IMultitool;
import alpvax.advancedautocrafting.item.multitool.MultitoolInst;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class MultitoolItem extends Item {
  public MultitoolItem(Item.Properties properties) {
    super(properties.maxStackSize(1));
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    MultitoolInst tool = nbt == null ? new MultitoolInst() : MultitoolInst.from(nbt.getCompound("Parent"));
    return new IMultitool.Provider(tool); //TODO: return other mod wrench capabilities?
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
    return true;
  }

  private IMultitool getMultiTool(ItemStack stack) {
    return stack.getCapability(Capabilities.MULTITOOL_CAPABILITY)
               .orElseThrow(() -> new NullPointerException("Multitool item does not have Multitool capability! HOW COULD THIS HAPPEN?!"));
  }

  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return false;
  }

  @Override
  public ItemStack getContainerItem(ItemStack itemStack)
  {
    return itemStack;
  }

  @Override
  public String getHighlightTip(ItemStack item, String displayName)
  {
    return displayName;//TODO
  }
  @Override
  public CompoundNBT getShareTag(ItemStack stack)
  {
    return stack.getTag(); //TODO
  }

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    //TODO: tick items
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    ItemStack s = getMultiTool(stack).selectToolForEntity(playerIn, target);
    return s != null && s.interactWithEntity(playerIn, target, hand) || super.itemInteractionForEntity(stack, playerIn, target, hand);
  }

  /*TODO: implement:
  onItemRightClick
      onItemUse
  onItemUseFinish
      onItemUseFirst
  onLeftClickEntity
      onPlayerStoppedUsing
  onUsingTick
   */

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    return getMultiTool(stack).allTools()
               .map(i -> i.getDestroySpeed(state))
               .max(Float::compareTo)
               .orElse(super.getDestroySpeed(stack, state));
  }

  @Nonnull
  @Override
  public java.util.Set<net.minecraftforge.common.ToolType> getToolTypes(ItemStack stack) {
    return getMultiTool(stack).allTools()
               .flatMap(i -> i.getToolTypes().stream())
               .collect(Collectors.toSet());
  }

  @Override
  public int getHarvestLevel(ItemStack stack, @Nonnull ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    return getMultiTool(stack).allTools()
               .map(i -> i.getHarvestLevel(tool, player, blockState))
               .max(Integer::compareTo)
               .orElse(super.getHarvestLevel(stack, tool, player, blockState));
  }

  @Override
  public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    ItemStack s = getMultiTool(stack).selectWeapon(attacker, target);
    if (s != null) {
      if (attacker instanceof PlayerEntity) {
        s.hitEntity(target, (PlayerEntity)attacker);
        return true;
      } else {
        return s.getItem().hitEntity(stack, target, attacker);
      }
    } else {
      return super.hitEntity(stack, target, attacker);
    }
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    ItemStack s = getMultiTool(stack).selectToolForBlock(entityLiving, state, worldIn, pos);
    if (s != null) {
      if (entityLiving instanceof PlayerEntity) {
        s.onBlockDestroyed(worldIn, state, pos, (PlayerEntity)entityLiving);
        return true;
      } else {
        return s.getItem().onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
      }
    } else {
      return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }
  }
}
