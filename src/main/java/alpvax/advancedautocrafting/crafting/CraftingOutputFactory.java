package alpvax.advancedautocrafting.crafting;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

@FunctionalInterface
public interface CraftingOutputFactory
{
	public default @Nonnull ICraftingOutput fromNBT(@Nonnull CraftingPattern pattern, @Nonnull NBTTagCompound nbt)
	{
		ICraftingOutput output = create(pattern);
		output.deserializeNBT(nbt);
		return output;
	}

	public @Nonnull ICraftingOutput create(@Nonnull CraftingPattern pattern);
}
