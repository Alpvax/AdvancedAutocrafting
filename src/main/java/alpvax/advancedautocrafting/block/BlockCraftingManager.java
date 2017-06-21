package alpvax.advancedautocrafting.block;

import java.util.List;

import com.google.common.collect.Lists;

import alpvax.advancedautocrafting.core.AdvancedAutocrafting;
import alpvax.advancedautocrafting.core.AutocraftingGuiHandler;
import alpvax.advancedautocrafting.item.ItemCraftingLinker;
import alpvax.advancedautocrafting.tile.TileEntityCraftingManager;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCraftingManager extends Block
{

	public BlockCraftingManager()
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
		return new TileEntityCraftingManager();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		TileEntity te = world.getTileEntity(pos);
		return Lists.newArrayList(te instanceof TileEntityCraftingManager ? ((TileEntityCraftingManager)te).toItemStack(state) : new ItemStack(this));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		return tile instanceof TileEntityCraftingManager ? ((TileEntityCraftingManager)tile).toItemStack(state) : new ItemStack(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = playerIn.getHeldItem(hand);

        if (itemstack.getItem() == AdvancedAutocrafting.Items.CRAFTING_LINKER && playerIn.isSneaking())
        {
        	ItemCraftingLinker.setSelectedManager(itemstack, (TileEntityCraftingManager)worldIn.getTileEntity(pos));
            return true;
        }
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
			AutocraftingGuiHandler.GUI.CRAFTING_MANAGER.open(playerIn, worldIn, pos);
            return true;
        }
    }
}
