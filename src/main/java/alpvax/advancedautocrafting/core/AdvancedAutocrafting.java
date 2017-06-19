package alpvax.advancedautocrafting.core;

import alpvax.advancedautocrafting.block.BlockCraftingManager;
import alpvax.advancedautocrafting.item.ItemCraftingLinker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AdvancedAutocrafting.MOD_ID)
public class AdvancedAutocrafting
{
	public static final String MOD_ID = "advancedautocrafting";

	public void preInit(FMLPreInitializationEvent event)
	{

	}

	@EventBusSubscriber
	public static class Blocks
	{
		public static final Block CRAFTING_MANAGER = new BlockCraftingManager().setUnlocalizedName("craftingmanager").setRegistryName("craftingmanager");

		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(CRAFTING_MANAGER);
		}
	}

	@EventBusSubscriber
	public static class Items
	{
		public static final Item CRAFTING_LINKER = new ItemCraftingLinker().setUnlocalizedName("craftinglinker").setRegistryName("craftinglinker");

		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event)
		{
			event.getRegistry().register(CRAFTING_LINKER);
		}
	}
}
