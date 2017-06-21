package alpvax.advancedautocrafting.crafting;

import java.util.ArrayList;
import java.util.List;

public class CraftingPattern
{
	private List<ICraftingIngredient> ingredients = new ArrayList<>();

	public List<ICraftingIngredient> getIngredients()
	{
		return ingredients;
	}
}
