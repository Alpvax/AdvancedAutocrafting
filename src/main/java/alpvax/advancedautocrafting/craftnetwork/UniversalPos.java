package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Objects;

public class UniversalPos implements Comparable<UniversalPos> {
  private final IWorld world;
  private final BlockPos pos;

  public UniversalPos(@Nonnull IWorld world, @Nonnull BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  public boolean isLoaded() {
    return this.isLoaded(0);
  }
  public boolean isLoaded(int adjacentBlocks) {
    return getWorld().isAreaLoaded(getPos(), adjacentBlocks);
  }

  public World getWorld() {
    return world.getWorld();
  }

  public BlockPos getPos() {
    return pos;
  }

  public BlockState getState() {
    return getWorld().getBlockState(getPos());
  }

  /**
   * Convenience redirect to {@link IWorld#getTileEntity(BlockPos)}.
   * Use if you do not care what type the TE is (or you intend to cast it yourself).
   */
  @Nullable
  public TileEntity getTileEntityRaw() {
    return getWorld().getTileEntity(getPos());
  }
  /**
   * Will return the TileEntity returned by {@link IWorld#getTileEntity(BlockPos)}, but only if it matches the provided type.
   */
  @Nullable
  public <T extends TileEntity> T getTileEntity(TileEntityType<T> type) {
    return type.func_226986_a_(getWorld(), getPos());
  }
  /**
   * Convenience method to cast the TileEntity returned by {@link IWorld#getTileEntity(BlockPos)} to type T.
   * Unsafe. Will throw {@link ClassCastException} if cast is not possible.
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public <T extends TileEntity> T getTileEntity() {
    return (T) getTileEntityRaw();
  }
  /**
   * Attempts to cast the TileEntity returned by {@link IWorld#getTileEntity(BlockPos)} to the class provided.
   * Will throw {@link ClassCastException} if cast is not possible.
   */
  @Nullable
  public <T extends TileEntity> T getTileEntity(Class <T> type) {
    return type.cast(getTileEntityRaw());
  }

  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    TileEntity t = getTileEntityRaw();
    return t != null ? t.getCapability(cap, side) : LazyOptional.empty();
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof UniversalPos) {
      UniversalPos o = (UniversalPos) obj;
      return world.equals(o.world) && pos.equals(o.pos);
    }
    return false;
  }

  @Override
  public int compareTo(UniversalPos o) {
    return Comparator.<UniversalPos>comparingInt(up -> up.getWorld().getDimension().getType().getId())
        .thenComparing(UniversalPos::getPos).compare(this, o);
  }
}
