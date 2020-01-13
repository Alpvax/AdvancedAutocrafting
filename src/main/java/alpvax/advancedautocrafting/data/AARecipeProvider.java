package alpvax.advancedautocrafting.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AARecipeProvider extends RecipeProvider {

  public AARecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    ShapedRecipeBuilder.shapedRecipe(CONTROLLER.get())
        .patternLine("IGI").patternLine("GBG").patternLine("IGI")
        .key('I', Tags.Items.INGOTS_IRON)
        .key('G', Tags.Items.INGOTS_GOLD)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .setGroup("autocrafting")
        .addCriterion("has_glass", this.hasItem(Tags.Items.GLASS))
        .addCriterion("has_iron_block", this.hasItem(Tags.Items.STORAGE_BLOCKS_IRON))
        .build(consumer);
  }
}
