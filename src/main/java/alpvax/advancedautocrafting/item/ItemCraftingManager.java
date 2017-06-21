package alpvax.advancedautocrafting.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemCraftingManager extends ItemBlock
{
	public ItemCraftingManager(Block block)
	{
		super(block);
		setRegistryName(block.getRegistryName());
	}
}
