package alpvax.advancedautocrafting.crafting;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public abstract class CraftingIngredientFactory extends IForgeRegistryEntry.Impl<CraftingIngredientFactory>
{
	public @Nonnull ICraftingIngredient fromNBT(@Nonnull CraftingPattern pattern, @Nonnull NBTTagCompound nbt)
	{
		ICraftingIngredient ingredient = create(pattern);
		ingredient.deserializeNBT(nbt);
		return ingredient;
	}

	public abstract @Nonnull ICraftingIngredient create(@Nonnull CraftingPattern pattern);
}
