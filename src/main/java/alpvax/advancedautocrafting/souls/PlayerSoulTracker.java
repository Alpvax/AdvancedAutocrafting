package alpvax.advancedautocrafting.souls;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.item.AAItems;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerSoulTracker implements ISoulTracker {
  private static final UUID MODIFIER_ID = UUID.fromString("97cdc9ba-479b-4d8d-a81f-bab2eaa94de0");
  private final ServerPlayerEntity player;
  private final IAttributeInstance attribute;
  private final Object2IntOpenHashMap<UUID> otherPlayerSlices = new Object2IntOpenHashMap<>();
  private int slicesRemoved = 0;

  public PlayerSoulTracker(ServerPlayerEntity player) {
    this.player = player;
    attribute = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
  }

  @Override
  public boolean canHarvest(UUID targetID) {
    return (player.getUniqueID().equals(targetID) && attribute.getBaseValue() - slicesRemoved * 2 >= 4)
        || otherPlayerSlices.getOrDefault(targetID, 0) > 0;
  }

  @Nonnull
  @Override
  public ItemStack getSoulSliver(@Nullable UUID playerID) {
    if(playerID == null) {
      playerID = player.getUniqueID();
    }
    if (player.getUniqueID().equals(playerID) && attribute.getBaseValue() >= 2) {
      slicesRemoved++;
    } else if (otherPlayerSlices.getOrDefault(playerID, 0) > 0) {
        otherPlayerSlices.addTo(playerID, -1);
    } else {
      return ItemStack.EMPTY;
    }
    updateModifier();
    return makeSliver(playerID);
  }

  @Override
  public void addSliver(UUID playerID) {
    otherPlayerSlices.addTo(playerID, 1);
    updateModifier();
  }

  private ItemStack makeSliver(UUID playerID) {
    ItemStack stack = new ItemStack(AAItems.SOUL_SLIVER.get());
    stack.getCapability(Capabilities.SOUL_SLIVER_CAPABILITY)
        .orElseThrow(() -> new NullPointerException("Sliver item doesn not have sliver capability"))
        .setPlayerID(playerID);
    return stack;
  }

  private void updateModifier() {
    attribute.removeModifier(MODIFIER_ID);
    attribute.applyModifier(new AttributeModifier(
        MODIFIER_ID,
        "advancedautocrafting.soulslice.modifier",
        Math.max(2 - attribute.getBaseValue(), 2 * (otherPlayerSlices.values().stream().reduce((a, b) -> a + b).orElse(0) - slicesRemoved)),
        AttributeModifier.Operation.ADDITION
    ));
  }

  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
    private Supplier<PlayerSoulTracker> sup;

    public Provider(ServerPlayerEntity player) {
      this.sup = () -> new PlayerSoulTracker(player);
    }

    private final LazyOptional<PlayerSoulTracker> capability = LazyOptional.of(() -> this.sup.get());

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == Capabilities.SOUL_TRACKER_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      PlayerSoulTracker t = sup.get();
      nbt.putInt("slicesRemoved", t.slicesRemoved);
      ListNBT list = new ListNBT();
      t.otherPlayerSlices.forEach((id, num) -> {
        if (num > 0) {
          CompoundNBT tag = new CompoundNBT();
          tag.putUniqueId("owner", id);
          tag.putInt("quantity", num);
        }
      });
      nbt.put("other", list);
      return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      PlayerSoulTracker t = sup.get();
      t.slicesRemoved = nbt.getInt("slicesRemoved");
      ListNBT list = nbt.getList("other", Constants.NBT.TAG_COMPOUND);
      list.forEach(ltag -> {
        CompoundNBT tag = (CompoundNBT)ltag;
        t.otherPlayerSlices.put(
            tag.getUniqueId("owner"),
            tag.getInt("quantity")
        );
      });
      t.updateModifier();
    }
  }
}
