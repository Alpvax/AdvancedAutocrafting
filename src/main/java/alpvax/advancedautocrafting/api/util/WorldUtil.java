package alpvax.advancedautocrafting.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldUtil {
  private static WorldUtil INSTANCE = new WorldUtil();

  public static WorldUtil getInstance() {
    return INSTANCE;
  }

  /**
   * Retrieve a TileEntity of type T from the world.
   * Wrapper around {@linkplain IBlockReader#getTileEntity}.
   * Uses unchecked class cast, so may throw a ClassCastException.
   */
  @SuppressWarnings("unchecked")
  public <T extends TileEntity> Optional<T> getTileEntity(IBlockReader world, BlockPos pos) {
    return Optional.ofNullable((T) world.getTileEntity(pos));
  }

  /**
   * Retrieve a TileEntity of type T from the world.
   * Will return {@linkplain Optional#empty()} if tileEntity is the incorrect type.
   */
  public <T extends TileEntity> Optional<T> getTileEntity(IBlockReader world, BlockPos pos, TileEntityType<T> type) {
    return Optional.ofNullable(type.func_226986_a_(world, pos));
  }
  /**
   * Retrieve a TileEntity of type T from the world.
   * Will return {@linkplain Optional#empty()} if tileEntity is not a subclass of the given class.
   */
  public <T extends TileEntity> Optional<T> getTileEntity(IBlockReader world, BlockPos pos, Class<T> tileClass) {
    return Optional.ofNullable(world.getTileEntity(pos)).map(t -> tileClass.isInstance(t) ? tileClass.cast(t) : null);
  }

  /**
   * Wrapper to get a capability from a block
   * @param capability the Capability to request.
   * @param side the side to retrieve the capability from.
   * Will return {@linkplain LazyOptional#empty()} if there is no capability (or no TileEntity) of the specified type.
   */
  public <T> LazyOptional<T> getBlockCapability(Capability<T> capability, @Nullable Direction side, IBlockReader world, BlockPos pos) {
    return getTileEntity(world, pos).map(t -> t.getCapability(capability, side)).orElse(LazyOptional.empty());
  }
}
