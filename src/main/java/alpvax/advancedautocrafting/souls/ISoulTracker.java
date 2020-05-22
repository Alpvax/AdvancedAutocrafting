package alpvax.advancedautocrafting.souls;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface ISoulTracker {
  boolean canHarvest(UUID targetID);
  @Nonnull ItemStack getSoulSliver(@Nullable UUID targetID);

  void addSliver(UUID playerID);
}
