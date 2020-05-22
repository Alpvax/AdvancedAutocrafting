package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SoulSlicer extends Item {
  public SoulSlicer() {
    super(new Item.Properties().rarity(Rarity.EPIC));
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return true;
  }

  /*Block click
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return super.onItemUse(context);
  }*/

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack stack = playerIn.getHeldItem(handIn);
    if (handIn != Hand.MAIN_HAND || !playerIn.getHeldItemOffhand().isEmpty()
        /*!playerIn.getHeldItemOffhand().getCapability(Capabilities.SOUL_SLIVER_CAPABILITY)
            .map(sliver -> sliver.matches(playerIn)).orElseGet(() -> playerIn.getHeldItemOffhand().isEmpty())*/
    ) {
      playerIn.sendStatusMessage(
          new TranslationTextComponent(AATranslationKeys.SOUL_SLICER_ARRANGEMENT, stack.getDisplayName()),
          true
      );
      return ActionResult.resultPass(stack);
    }
    return playerIn.getCapability(Capabilities.SOUL_TRACKER_CAPABILITY).map(tracker -> {
      if (tracker.canHarvest(playerIn.getUniqueID())) {
        playerIn.setActiveHand(handIn);
        return ActionResult.resultSuccess(stack);
      } else {
        playerIn.sendStatusMessage(new TranslationTextComponent(AATranslationKeys.SOUL_NOT_ENOUGH), true);
        return ActionResult.resultPass(stack);
      }
    }).orElse(ActionResult.resultPass(stack));
  }

  @Nonnull
  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    entityLiving.getCapability(Capabilities.SOUL_TRACKER_CAPABILITY).ifPresent(tracker -> {
      UUID id = entityLiving.getUniqueID();
      if (tracker.canHarvest(id)) {
        entityLiving.setItemStackToSlot(EquipmentSlotType.OFFHAND, tracker.getSoulSliver(id));
      } else if (entityLiving instanceof PlayerEntity){
        ((PlayerEntity)entityLiving).sendStatusMessage(new TranslationTextComponent(AATranslationKeys.SOUL_NOT_ENOUGH), true);
      }
    });
    return stack;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    return hand == Hand.MAIN_HAND && tryHarvestFrom(playerIn, target);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
    return tryHarvestFrom(player, entity);
  }

  private boolean tryHarvestFrom(PlayerEntity player, Entity target) {
    if (player.world.isRemote) return false;
    return target.getCapability(Capabilities.SOUL_TRACKER_CAPABILITY).map(tracker -> {
      UUID playerID = player.getUniqueID();
      if (tracker.canHarvest(playerID)) {
        target.entityDropItem(tracker.getSoulSliver(playerID), 0.5F);
        return true;
      }
      return false;
    }).orElse(false);
  }

  @Nonnull
  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.DRINK;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return 50;
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
  }
}
