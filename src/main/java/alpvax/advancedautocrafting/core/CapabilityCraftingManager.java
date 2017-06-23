package alpvax.advancedautocrafting.core;

import alpvax.advancedautocrafting.crafting.ICraftingRecipeManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityCraftingManager
{
	@CapabilityInject(ICraftingRecipeManager.class)
	public static Capability<ICraftingRecipeManager> CAPABILITY = null;

	public static void register()
	{
		CapabilityManager.INSTANCE.register(ICraftingRecipeManager.class, new IStorage<ICraftingRecipeManager>()
		{
			@Override
			public NBTBase writeNBT(Capability<ICraftingRecipeManager> capability, ICraftingRecipeManager instance, EnumFacing side)
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void readNBT(Capability<ICraftingRecipeManager> capability, ICraftingRecipeManager instance, EnumFacing side, NBTBase nbt)
			{
				// TODO Auto-generated method stub

			}
		}, ICraftingRecipeManager.Impl::new);
	}
}
