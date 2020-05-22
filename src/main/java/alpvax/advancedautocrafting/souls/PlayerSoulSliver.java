package alpvax.advancedautocrafting.souls;

import alpvax.advancedautocrafting.AAUtil;
import alpvax.advancedautocrafting.Capabilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerSoulSliver {
  private UUID playerID;
  private final Set<UUID> consumableBy = new HashSet<UUID>();

  public PlayerSoulSliver() {
  }
  public PlayerSoulSliver(UUID playerID) {
    this();
    setPlayerID(playerID);
  }

  @Nullable
  public String getPlayerName() {
    if (playerID != null) {
      return AAUtil.getProfile(playerID).getName();
    }
    return null;
  }

  public boolean canConsume(LivingEntity entity) {
    UUID pid = entity.getUniqueID();
    return playerID == pid || consumableBy.contains(pid);
  }

  public void consume(LivingEntity entity) {
    entity.getCapability(Capabilities.SOUL_TRACKER_CAPABILITY).ifPresent(tracker -> {
      tracker.addSliver(playerID);
    });
  }

  public boolean matches(PlayerEntity player) {
    return playerID.equals(player.getUniqueID());
  }

  void setPlayerID(UUID playerID) {
    this.playerID = playerID;
  }

  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
    private Supplier<PlayerSoulSliver> sup;

    public Provider(@Nullable CompoundNBT nbt) {
      this(() -> Capabilities.SOUL_SLIVER_CAPABILITY.getDefaultInstance());
      if (nbt != null) {
        deserializeNBT(nbt);
      }
    }
    Provider(Supplier<PlayerSoulSliver> sup) {
      this.sup = sup;
    }
    public Provider(PlayerEntity player) {
      this(() -> new PlayerSoulSliver(player.getUniqueID()));
    }

    private final LazyOptional<PlayerSoulSliver> capability = LazyOptional.of(() -> this.sup.get());

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == Capabilities.SOUL_SLIVER_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      PlayerSoulSliver s = sup.get();
      if (s.playerID != null) {
        nbt.putUniqueId("owner", s.playerID);
        if (s.consumableBy.size() > 0) {
          ListNBT list = new ListNBT();
          s.consumableBy.forEach(uuid -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("id", uuid);
          });
          nbt.put("consumableby", list);
        }
      }
      return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      PlayerSoulSliver s = sup.get();
      s.setPlayerID(nbt.getUniqueId("owner"));
      if (nbt.contains("consumableby", Constants.NBT.TAG_LIST)) {
        nbt.getList("consumableby", Constants.NBT.TAG_COMPOUND).forEach(t -> {
          CompoundNBT tag = (CompoundNBT) t;
          s.consumableBy.add(tag.getUniqueId("id"));
        });
      }
    }
  }
}
