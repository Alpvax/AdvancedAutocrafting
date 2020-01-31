package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INetworkNode {
  @Nonnull NonNullList<INetworkNode> getChildNodes(Direction inbound);
  @Nonnull BlockPos getPos();
  @Nonnull World getWorld();

  default double getX() { return getPos().getX(); }
  default double getY() { return getPos().getY(); }
  default double getZ() { return getPos().getZ(); }

  @Nonnull
  default BlockState getBlockState() { return getWorld().getBlockState(getPos()); }

  default <T extends TileEntity> T getTileEntity() { return (T) getWorld().getTileEntity(getPos()); }

  default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
    return getCapability(cap, null);
  }
  default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    TileEntity tile = getTileEntity();
    return tile == null ? LazyOptional.empty() : tile.getCapability(cap, side);
  }
}
