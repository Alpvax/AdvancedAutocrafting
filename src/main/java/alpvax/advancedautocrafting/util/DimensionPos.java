package alpvax.advancedautocrafting.util;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;

public class DimensionPos implements INBTSerializable<NBTTagCompound>
{
	private int dimension;
	private BlockPos pos;

	public DimensionPos(NBTTagCompound nbt)
	{
		deserializeNBT(nbt);
	}

	public DimensionPos(World world, BlockPos pos)
	{
		this(world.provider.getDimension(), pos);
	}

	public DimensionPos(int dimension, BlockPos pos)
	{
		this.dimension = dimension;
		this.pos = pos;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Dimension", dimension);
		nbt.setTag("Pos", NBTUtil.createPosTag(pos));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		dimension = nbt.getInteger("Dimension");
		pos = NBTUtil.getPosFromTag(nbt.getCompoundTag("Pos"));
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public int getDimension()
	{
		return dimension;
	}

	public World getWorld()
	{
		return DimensionManager.getWorld(dimension);
	}

	public boolean isSameWorld(World world)
	{
		return world != null && world.provider.getDimension() == dimension;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(dimension, pos);
	}

	@Override
	public boolean equals(Object other)
	{
		if(other == null || !(other instanceof DimensionPos))
		{
			return false;
		}
		DimensionPos dp = (DimensionPos)other;
		return dp.dimension == dimension && dp.pos.equals(pos);
	}
}
