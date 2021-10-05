package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AARecipeProvider extends RecipeProvider {

  public AARecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    CriterionTriggerInstance hasIronBlock = has(Tags.Items.STORAGE_BLOCKS_IRON);
    CriterionTriggerInstance hasPearl = has(Tags.Items.ENDER_PEARLS);
    CriterionTriggerInstance hasIronBars = has(Items.IRON_BARS);
    ShapedRecipeBuilder.shaped(CONTROLLER.get())
        .pattern("IGI").pattern("GBG").pattern("IGI")
        .define('I', Tags.Items.INGOTS_IRON)
        .define('G', Tags.Items.INGOTS_GOLD)
        .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .unlockedBy("has_glass", has(Tags.Items.GLASS))
        .unlockedBy("has_iron_block", hasIronBlock)
        .save(consumer);
    ShapedRecipeBuilder.shaped(REMOTE_MARKER.get())
        .pattern("IPI").pattern("PBP").pattern("IPI")
        .define('I', Items.IRON_BARS)
        .define('P', Tags.Items.ENDER_PEARLS)
        .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .unlockedBy("has_iron_bars", hasIronBars)
        .unlockedBy("has_pearl", hasPearl)
        .unlockedBy("has_iron_block", hasIronBlock)
        .group(REMOTE_MARKER.getId().toString())
        .save(consumer);
    ShapelessRecipeBuilder.shapeless(REMOTE_MARKER.get())
        .requires(AAItems.REMOTE_POS.get())
        .unlockedBy("has_pos_marker", has(AAItems.REMOTE_POS.get()))
        .group(REMOTE_MARKER.getId().toString())
        .save(consumer, new ResourceLocation(AdvancedAutocrafting.MODID, REMOTE_MARKER.getId().getPath() + "_from_marker"));
    ShapedRecipeBuilder.shaped(REMOTE_MASTER.get())
        .pattern("PGP").pattern("GBG").pattern("PGP")
        .define('P', Tags.Items.ENDER_PEARLS)
        .define('G', Tags.Items.INGOTS_GOLD)
        .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .unlockedBy("has_pearl", hasPearl)
        .unlockedBy("has_iron_block", hasIronBlock)
        .save(consumer);
    ShapedRecipeBuilder.shaped(WIRE.get(), 16)
        .pattern(" I ").pattern("IBI").pattern(" I ")
        .define('I', Items.IRON_BARS)
        .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
        .unlockedBy("has_iron_bars", hasIronBars)
        .unlockedBy("has_iron_block", hasIronBlock)
        .save(consumer);
    ShapedRecipeBuilder.shaped(AAItems.MULTITOOL.get())
        .pattern("IR ").pattern("RIR").pattern(" RI")
        .define('I', Tags.Items.INGOTS_IRON)
        .define('R', Tags.Items.DYES_RED)
        .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
        .unlockedBy("has_red_dye", has(Tags.Items.DYES_RED))
        .save(consumer);
  }
}
