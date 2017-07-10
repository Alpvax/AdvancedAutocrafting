package alpvax.advancedautocrafting.crafting;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class CraftingOutputFactory extends IForgeRegistryEntry.Impl<CraftingOutputFactory>
{
	public @Nonnull ICraftingOutput fromNBT(@Nonnull CraftingPattern pattern, @Nonnull NBTTagCompound nbt)
	{
		ICraftingOutput output = create(pattern);
		output.deserializeNBT(nbt);
		return output;
	}

	public abstract @Nonnull ICraftingOutput create(@Nonnull CraftingPattern pattern);
}
