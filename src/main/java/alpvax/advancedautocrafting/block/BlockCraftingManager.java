package alpvax.advancedautocrafting.block;

import java.util.List;

import com.google.common.collect.Lists;

import alpvax.advancedautocrafting.tile.TileEntityCraftingManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCraftingManager extends Block
{

	public BlockCraftingManager()
	{
		super(Material.IRON);
		// TODO Auto-generated constructor stub
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
}
