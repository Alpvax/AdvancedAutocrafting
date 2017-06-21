package alpvax.advancedautocrafting.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alpvax.advancedautocrafting.tile.TileEntityCraftingManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCraftingLinker extends Item
{
	public static void setSelectedManager(@Nonnull ItemStack stack, @Nullable TileEntityCraftingManager manager)
	{
		if(manager != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Dimension", manager.getWorld().provider.getDimension());
			nbt.setTag("Pos", NBTUtil.createPosTag(manager.getPos()));
			stack.setTagInfo("SelectedCraftingManager", nbt);
		}
		else
		{
			stack.getTagCompound().removeTag("SelectedCraftingManager");
		}
	}

	public static @Nullable TileEntityCraftingManager getSelectedManager(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getSubCompound("SelectedCraftingManager");
		if(nbt != null)
		{
			World world = DimensionManager.getWorld(nbt.getInteger("Dimension"));
			if(world != null)
			{
				TileEntity tile = world.getTileEntity(NBTUtil.getPosFromTag(nbt.getCompoundTag("Pos")));
				if(tile instanceof TileEntityCraftingManager)
				{
					return (TileEntityCraftingManager)tile;
				}
			}
		}
		return null;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return world.getTileEntity(pos) instanceof TileEntityCraftingManager;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		TileEntityCraftingManager tile = getSelectedManager(stack);
		if(tile == null)
		{
			tooltip.add(I18n.format("item.selectedmanager.none"));
		}
		else
		{
			int worldID = tile.getWorld().provider.getDimension();
			if(worldID == playerIn.world.provider.getDimension())
			{
				tooltip.add(I18n.format("item.selectedmanager.samedim", tile.getPos()));
			}
			else
			{
				tooltip.add(I18n.format("item.selectedmanager.diffdim", tile.getPos(), worldID));
			}
		}
	}
}
