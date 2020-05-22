package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.souls.PlayerSoulSliver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoulSliver extends Item {
  public SoulSliver() {
    super(new Item.Properties().rarity(Rarity.RARE));
  }

  /*@Override
  public boolean hasCustomEntity(ItemStack stack) {
    return true;
  }

  @Nullable
  @Override
  public Entity createEntity(World world, Entity location, ItemStack itemstack) {
    return null;//TODO
  }*/

  @Nonnull
  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.EAT;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return 32;
  }

  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack stack = playerIn.getHeldItem(handIn);
    return stack.getCapability(Capabilities.SOUL_SLIVER_CAPABILITY).map(sliver -> {
      if (sliver.canConsume(playerIn)) {
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
      } else {
        playerIn.sendStatusMessage(new TranslationTextComponent(AATranslationKeys.SOUL_NOT_CONSUMABLE), true);
        return ActionResult.resultPass(stack);
      }
    }).orElseGet(() -> ActionResult.resultSuccess(playerIn.getHeldItem(handIn)));
  }

  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    entityLiving.getCapability(Capabilities.SOUL_SLIVER_CAPABILITY).ifPresent(sliver -> {
      if (sliver.canConsume(entityLiving)) {
        sliver.consume(entityLiving);
        stack.shrink(1);
      } else if (entityLiving instanceof PlayerEntity){
        ((PlayerEntity)entityLiving).sendStatusMessage(new TranslationTextComponent(AATranslationKeys.SOUL_NOT_CONSUMABLE), true);
      }
    });
    return stack.isEmpty() ? ItemStack.EMPTY : stack;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  /*@Nullable
  @Override
  public CompoundNBT getShareTag(ItemStack stack) {
    CompoundNBT nbt = super.getShareTag(stack);
    nb
    return nbt;
  }

  @Override
  public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {

  }*/

  public ITextComponent getDisplayName(ItemStack stack) {
    return stack.getCapability(Capabilities.SOUL_SLIVER_CAPABILITY).map(
        sliver -> (ITextComponent)new TranslationTextComponent(AATranslationKeys.PLAYER_SOUL, sliver.getPlayerName())
    ).orElseGet(() -> super.getDisplayName(stack));
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return new PlayerSoulSliver.Provider(nbt);
  }
}
