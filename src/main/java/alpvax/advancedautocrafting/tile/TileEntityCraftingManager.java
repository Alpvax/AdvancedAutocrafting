package alpvax.advancedautocrafting.tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import alpvax.advancedautocrafting.core.AdvancedAutocrafting;
import alpvax.advancedautocrafting.core.CapabilityCraftingManager;
import alpvax.advancedautocrafting.crafting.ICraftingRecipeManager;
import alpvax.advancedautocrafting.item.ItemCraftingLinker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityCraftingManager extends TileEntity
{
	private ICraftingRecipeManager recipeManager; //TODO:Instantiate

	private Set<BlockPos> linkedBlocks = new HashSet<>();

	private static void sendSingleChatLine(World worldIn, ITextComponent chatmessage)
	{
		if(worldIn.isRemote)
		{
			AdvancedAutocrafting.proxy.sendPlayerChatMessage(chatmessage, 698545369);
		}
	}

	public ItemStack toItemStack(IBlockState state)
	{
		ItemStack stack = new ItemStack(state.getBlock());
		writeNBT(stack.getOrCreateSubCompound("BlockEntityTag"));
		return stack;
	}

	private NBTTagCompound writeNBT(NBTTagCompound nbt)
	{
		NBTTagList blocksList = new NBTTagList();
		for(BlockPos pos : linkedBlocks)
		{
			blocksList.appendTag(NBTUtil.createPosTag(pos));
		}
		if(!blocksList.hasNoTags())
		{
			nbt.setTag("Blocks", blocksList);
		}
		/*TODO:NBTTagList patternList = new NBTTagList();
		if(!patternList.hasNoTags())
		{
			nbt.setTag("Patterns", patternList);
		}*/
		return nbt;
	}

	private void readNBT(NBTTagCompound nbt)
	{
		NBTTagList blocksList = nbt.getTagList("Blocks", NBT.TAG_LIST);
		linkedBlocks.clear();
		if(!blocksList.hasNoTags())
		{
			for(int i = 0; i < blocksList.tagCount(); i++)
			{
				linkedBlocks.add(NBTUtil.getPosFromTag(blocksList.getCompoundTagAt(i)));
			}
		}
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

	public boolean linkBlock(World worldIn, BlockPos pos, EnumFacing facing)
	{
		if(!canConnectToWorld(worldIn))
		{
			sendSingleChatLine(worldIn, new TextComponentTranslation("manager.link.wrongworld", worldIn.provider.getDimension(), getWorld().provider.getDimension()).setStyle(ItemCraftingLinker.ERROR_STYLE));
			return false;
		}
		if(worldIn.isRemote)
		{
			return true;
		}
		if(linkedBlocks.contains(pos))
		{
			linkedBlocks.remove(pos);
			sendSingleChatLine(worldIn, new TextComponentTranslation("manager.link.removed", worldIn.getBlockState(pos), getPos()));
		}
		else
		{
			linkedBlocks.add(pos);
			sendSingleChatLine(worldIn, new TextComponentTranslation("manager.link.added", worldIn.getBlockState(pos), getPos()));
		}
		markDirty();
		return true;
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

	public Set<BlockPos> getLinkedBlocks()
	{
		return Collections.unmodifiableSet(linkedBlocks);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityCraftingManager.CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityCraftingManager.CAPABILITY ? CapabilityCraftingManager.CAPABILITY.cast(recipeManager) : super.getCapability(capability, facing);
	}
}
