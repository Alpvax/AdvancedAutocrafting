package alpvax.advancedautocrafting.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICraftingIngredient extends INBTSerializable<NBTTagCompound>
{
	public CraftingPattern getPattern();

	public boolean canFulfill();

	public boolean consumeIngredient();
}
