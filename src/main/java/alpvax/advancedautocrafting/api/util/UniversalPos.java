package alpvax.advancedautocrafting.api.util;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Objects;

class UniversalPos extends ProxyBlockSource implements Comparable<IBlockSource> {
  public static final Comparator<IBlockSource> COMPARATOR = Comparator.comparing((IBlockSource loc) -> loc.getLevel().dimension())
                                                                 .thenComparing(loc -> new BlockPos(loc.x(), loc.y(), loc.z()));

  public static UniversalPos from(@Nonnull IWorldPosCallable c) {
    return c.evaluate(UniversalPos::new).orElseThrow(() -> new NullPointerException("Failed to create UniversalPos from IWorldPosCallable: " + c.toString()));
  }
  public UniversalPos(@Nonnull World world, @Nonnull BlockPos pos) {
    super((ServerWorld) world, pos); //TODO: Fix ServerWorld not on Client
  }

  public boolean isLoaded() {
    return this.isLoaded(0);
  }
  public boolean isLoaded(int adjacentBlocks) {
    return getLevel().isAreaLoaded(getPos(), adjacentBlocks);
  }

  /**
   * Equivalent of {@link BlockPos#offset}
   * @return a new UniversalPos with the same world, and the offset pos
   */
  public UniversalPos offset(Direction d) {
    return new UniversalPos(getLevel(), getPos().relative(d));
  }

  /**
   * Convenience method to get a capability from the TileEntity at this postion.
   * @see TileEntity#getCapability(Capability, Direction).
   * @return the value from {@link TileEntity#getCapability} or {@link LazyOptional#empty()} if there is no TileEntity at that position.
   */
  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    TileEntity t = getEntity();
    //noinspection ConstantConditions
    return t != null ? t.getCapability(cap, side) : LazyOptional.empty();
  }

  public ITextComponent singleLineDisplay() {
    return new StringTextComponent("Dimension: \"" + getLevel().dimension().location().toString()
                                       + "\"; Position: " + getPos() + ";");//TODO: Convert to translation?
  }

  @Override
  public int hashCode() {
    BlockPos pos = getPos();
    return Objects.hash(getLevel(), new ChunkPos(pos), pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof UniversalPos) {
      UniversalPos o = (UniversalPos) obj;
      return getLevel().equals(o.getLevel()) && getPos().equals(o.getPos());
    }
    return false;
  }

  @Override
  public int compareTo(@Nonnull IBlockSource o) {
    return COMPARATOR.compare(this, o);
  }
}