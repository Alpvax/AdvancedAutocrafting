package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AARecipeProvider extends RecipeProvider {

  public AARecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    ICriterionInstance hasIronBlock = hasItem(Tags.Items.STORAGE_BLOCKS_IRON);
    ICriterionInstance hasPearl = hasItem(Tags.Items.ENDER_PEARLS);
    ICriterionInstance hasIronBars = hasItem(Items.IRON_BARS);
    ShapedRecipeBuilder.shapedRecipe(CONTROLLER.get())
        .patternLine("IGI").patternLine("GBG").patternLine("IGI")
        .key('I', Tags.Items.INGOTS_IRON)
        .key('G', Tags.Items.INGOTS_GOLD)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .addCriterion("has_glass", this.hasItem(Tags.Items.GLASS))
        .addCriterion("has_iron_block", hasIronBlock)
        .build(consumer);
    ShapedRecipeBuilder.shapedRecipe(REMOTE_MARKER.get())
        .patternLine("IPI").patternLine("PBP").patternLine("IPI")
        .key('I', Items.IRON_BARS)
        .key('P', Tags.Items.ENDER_PEARLS)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .addCriterion("has_iron_bars", hasIronBars)
        .addCriterion("has_pearl", hasPearl)
        .addCriterion("has_iron_block", hasIronBlock)
        .setGroup(REMOTE_MARKER.getId().toString())
        .build(consumer);
    ShapelessRecipeBuilder.shapelessRecipe(REMOTE_MARKER.get())
        .addIngredient(AAItems.REMOTE_POS.get())
        .addCriterion("has_pos_marker", hasItem(AAItems.REMOTE_POS.get()))
        .setGroup(REMOTE_MARKER.getId().toString())
        .build(consumer, new ResourceLocation(AdvancedAutocrafting.MODID, REMOTE_MARKER.getId().getPath() + "_from_marker"));
    ShapedRecipeBuilder.shapedRecipe(REMOTE_MASTER.get())
        .patternLine("PGP").patternLine("GBG").patternLine("PGP")
        .key('P', Tags.Items.ENDER_PEARLS)
        .key('G', Tags.Items.INGOTS_GOLD)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .addCriterion("has_pearl", hasPearl)
        .addCriterion("has_iron_block", hasIronBlock)
        .build(consumer);
    ShapedRecipeBuilder.shapedRecipe(WIRE.get(), 16)
        .patternLine(" I ").patternLine("IBI").patternLine(" I ")
        .key('I', Items.IRON_BARS)
        .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .addCriterion("has_iron_bars", hasIronBars)
        .addCriterion("has_iron_block", hasIronBlock)
        .build(consumer);
    ShapedRecipeBuilder.shapedRecipe(AAItems.MULTITOOL.get())
        .patternLine("IR ").patternLine("RIR").patternLine(" RI")
        .key('I', Tags.Items.INGOTS_IRON)
        .key('R', Tags.Items.DYES_RED)
        .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
        .addCriterion("has_red_dye", hasItem(Tags.Items.DYES_RED))
        .build(consumer);
  }
}
