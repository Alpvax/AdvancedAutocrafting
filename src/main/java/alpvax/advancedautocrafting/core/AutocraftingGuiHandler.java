package alpvax.advancedautocrafting.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class AutocraftingGuiHandler implements IGuiHandler
{
	public enum GUI
	{
		CRAFTING_MANAGER;

		public void open(EntityPlayer player, World world, BlockPos pos)
		{
			player.openGui(AdvancedAutocrafting.instance, ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
