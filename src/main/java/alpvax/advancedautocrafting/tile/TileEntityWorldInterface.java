package alpvax.advancedautocrafting.tile;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

import alpvax.advancedautocrafting.crafting.IWorldInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityWorldInterface extends TileEntity
{
	private BlockPos managerPos;
	private IWorldInterface handler;

	public ItemStack toItemStack(IBlockState state)
	{
		ItemStack stack = new ItemStack(state.getBlock());
		writeNBT(stack.getOrCreateSubCompound("BlockEntityTag"));
		return stack;
	}


	private NBTTagCompound writeNBT(NBTTagCompound nbt)
	{
		nbt.setTag("ManagerPos", NBTUtil.createPosTag(managerPos));
		return nbt;
	}

	private void readNBT(NBTTagCompound nbt)
	{
		managerPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("ManagerPos"));
		/*TODO:NBTTagList patternList = nbt.getTagList("Patterns", NBT.TAG_LIST);
		if(!patternList.hasNoTags())
		{
			for(int i = 0; i < patternList.tagCount(); i++)
			{
				//TODO: add pattern(patternList.getCompoundTagAt(i)));
			}
		}*/
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		readNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		return writeNBT(super.writeToNBT(compound));
	}

	public boolean canConnectToWorld(World world)
	{
		return getWorld().provider.getDimension() == world.provider.getDimension();
	}

	public void linkBlock(BlockPos pos)
	{
		if(!Objects.equal(managerPos, pos))
		{
			managerPos = pos;
			markDirty();
		}
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 6, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeNBT(super.getUpdateTag());
	}

	public BlockPos getLinkedManagerPos()
	{
		return managerPos;
	}

	/*@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityCraftingManager.CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityCraftingManager.CAPABILITY ? CapabilityCraftingManager.CAPABILITY.cast(recipeManager) : super.getCapability(capability, facing);
	}*/
}
