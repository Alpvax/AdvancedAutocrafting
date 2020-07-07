package alpvax.advancedautocrafting.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITileEntityProvider<T extends IBlockReader> {
  @Nonnull T getWorld();
  @Nonnull BlockPos getPos();

  /**
   * Convenience redirect to {@link IBlockReader#getTileEntity(BlockPos)}.
   * Use if you do not care what type the TE is (or you intend to cast it yourself).
   */
  @Nullable
  default TileEntity getTileEntityRaw() {
    return getWorld().getTileEntity(getPos());
  }
  /**
   * Will return the TileEntity returned by {@link IBlockReader#getTileEntity(BlockPos)}, but only if it matches the provided type.
   */
  @Nullable
  default <T extends TileEntity> T getTileEntity(TileEntityType<T> type) {
    return type.func_226986_a_(getWorld(), getPos());
  }
  /**
   * Convenience method to cast the TileEntity returned by {@link IBlockReader#getTileEntity(BlockPos)} to type T.
   * Unsafe. Will throw {@link ClassCastException} if cast is not possible.
   */
  @Nullable
  @SuppressWarnings("unchecked")
  default <T extends TileEntity> T getTileEntity() {
    return (T) getTileEntityRaw();
  }
  /**
   * Attempts to cast the TileEntity returned by {@link IBlockReader#getTileEntity(BlockPos)} to the class provided.
   * Will throw {@link ClassCastException} if cast is not possible.
   */
  @Nullable
  default <T extends TileEntity> T getTileEntity(Class <T> type) {
    return type.cast(getTileEntityRaw());
  }

  /**
   * Convenience method to get a capability from the TileEntity at this postion.
   * @see TileEntity#getCapability(Capability, Direction).
   * @return the value from {@link TileEntity#getCapability} or {@link LazyOptional#empty()} if there is no TileEntity at that position.
   */
  @Nonnull
  default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    TileEntity t = getTileEntityRaw();
    return t != null ? t.getCapability(cap, side) : LazyOptional.empty();
  }

  static ITileEntityProvider<World> from(IWorldPosCallable callable) {
    return callable.apply((w, p) ->
        w == null || p != null ? null : new ITileEntityProvider<World>() {
          @Nonnull
          @Override
          public World getWorld() {
            return w;
          }

          @Nonnull
          @Override
          public BlockPos getPos() {
            return p;
          }
        }
    ).orElseThrow(NullPointerException::new);
  }
}
