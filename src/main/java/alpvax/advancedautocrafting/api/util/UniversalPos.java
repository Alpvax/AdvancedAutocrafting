package alpvax.advancedautocrafting.api.util;

import net.minecraft.dispenser.ILocation;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

public class UniversalPos extends ProxyBlockSource implements Comparable<ILocation> {
  public static final Comparator<ILocation> COMPARATOR = Comparator.comparing((ILocation loc) -> loc.getWorld().func_234923_W_())
                                                                 .thenComparing(loc -> new BlockPos(loc.getX(), loc.getY(), loc.getZ()));

  public static UniversalPos from(@Nonnull IWorldPosCallable c) {
    return c.apply(UniversalPos::new).orElseThrow(() -> new NullPointerException("Failed to create UniversalPos from IWorldPosCallable: " + c.toString()));
  }
  public UniversalPos(@Nonnull World world, @Nonnull BlockPos pos) {
    super(world, pos);
  }

  public boolean isLoaded() {
    return this.isLoaded(0);
  }
  public boolean isLoaded(int adjacentBlocks) {
    return getWorld().isAreaLoaded(getBlockPos(), adjacentBlocks);
  }

  /**
   * Equivalent of {@link BlockPos#offset}
   * @return a new UniversalPos with the same world, and the offset pos
   */
  public UniversalPos offset(Direction d) {
    return new UniversalPos(getWorld(), getBlockPos().offset(d));
  }

  public ITextComponent singleLineDisplay() {
    return new StringTextComponent("Dimension: \"" + getWorld().func_234923_W_().func_240901_a_().toString()
                                       + "\"; Position: " + getBlockPos() + ";");//TODO: Convert to translation?
  }

  @Override
  public int hashCode() {
    BlockPos pos = getBlockPos();
    return Objects.hash(getWorld(), new ChunkPos(pos), pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof UniversalPos) {
      UniversalPos o = (UniversalPos) obj;
      return getWorld().equals(o.getWorld()) && getBlockPos().equals(o.getBlockPos());
    }
    return false;
  }

  @Override
  public int compareTo(@Nonnull ILocation o) {
    return COMPARATOR.compare(this, o);
  }
}