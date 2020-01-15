package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static alpvax.advancedautocrafting.block.AABlocks.CONTROLLER;
import static alpvax.advancedautocrafting.block.AABlocks.REMOTE_MARKER;

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
        .addCriterion("has_glass", this.hasItem(Tags.Items.GLASS))
        .addCriterion("has_iron_block", this.hasItem(Tags.Items.STORAGE_BLOCKS_IRON))
        .build(consumer);
    ShapedRecipeBuilder.shapedRecipe(REMOTE_MARKER.get())
        .patternLine("IPI").patternLine("PBP").patternLine("IPI")
        .key('I', Items.IRON_BARS)
        .key('P', Tags.Items.ENDER_PEARLS)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .addCriterion("has_iron_bars", this.hasItem(Items.IRON_BARS))
        .addCriterion("has_iron_block", this.hasItem(Tags.Items.STORAGE_BLOCKS_IRON))
        .build(consumer);
    ShapelessRecipeBuilder.shapelessRecipe(REMOTE_MARKER.get())
        .addIngredient(AAItems.REMOTE_POS.get())
        .addCriterion("has_pos_marker", hasItem(AAItems.REMOTE_POS.get()))
        .build(consumer, new ResourceLocation(AdvancedAutocrafting.MODID, REMOTE_MARKER.getId().getPath() + "_from_marker"));
  }
}
