package alpvax.advancedautocrafting.crafting;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

@FunctionalInterface
public interface CraftingIngredientFactory
{
	public default @Nonnull ICraftingIngredient fromNBT(@Nonnull CraftingPattern pattern, @Nonnull NBTTagCompound nbt)
	{
		ICraftingIngredient ingredient = create(pattern);
		ingredient.deserializeNBT(nbt);
		return ingredient;
	}

	public @Nonnull ICraftingIngredient create(@Nonnull CraftingPattern pattern);
}
