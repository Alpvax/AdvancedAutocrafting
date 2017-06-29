package alpvax.advancedautocrafting.block;

import java.util.List;

import com.google.common.collect.Lists;

import alpvax.advancedautocrafting.core.AdvancedAutocrafting;
import alpvax.advancedautocrafting.core.AutocraftingGuiHandler;
import alpvax.advancedautocrafting.tile.TileEntityCraftingManager;
import alpvax.advancedautocrafting.tile.TileEntityWorldInterface;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWorldInterface extends Block
{

	public BlockWorldInterface()
	{
		super(Material.IRON);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityWorldInterface();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		TileEntity te = world.getTileEntity(pos);
		return Lists.newArrayList(te instanceof TileEntityWorldInterface ? ((TileEntityWorldInterface)te).toItemStack(state) : new ItemStack(this));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		return tile instanceof TileEntityWorldInterface ? ((TileEntityWorldInterface)tile).toItemStack(state) : new ItemStack(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
		{
			TileEntityCraftingManager tile = (TileEntityCraftingManager)worldIn.getTileEntity(pos);
			AdvancedAutocrafting.proxy.sendPlayerChatMessage(new TextComponentString("CLIENT: " + tile.getLinkedBlocks().toString()), 29867296);//XXX
			return true;
		}
		else
		{
			playerIn.sendMessage(new TextComponentString("SERVER: " + ((TileEntityCraftingManager)worldIn.getTileEntity(pos)).getLinkedBlocks().toString()));//XXX
			AutocraftingGuiHandler.GUI.CRAFTING_MANAGER.open(playerIn, worldIn, pos);
			return true;
		}
	}

}
