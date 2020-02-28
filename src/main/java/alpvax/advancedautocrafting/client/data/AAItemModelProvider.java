package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

/**
 * Credits to the Tropicraft team, for their sample and helper methods.
 */
public class AAItemModelProvider extends ItemModelProvider {

  public AAItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, AdvancedAutocrafting.MODID, existingFileHelper);
  }

  @Override
  public String getName() {
    return "Advanced Autocrafting Item Models";
  }

  @Override
  protected void registerModels() {

    generated(AAItems.REMOTE_POS);
    handheld(AAItems.MULTITOOL);

    // BLOCKS
    blockItem(AABlocks.CONTROLLER);
    blockItem(AABlocks.REMOTE_MARKER);
    blockItem(AABlocks.REMOTE_MASTER);
    //blockItem(AABlocks.WIRE, "_core");
  }



  private String name(Supplier<? extends IItemProvider> item) {
    return item.get().asItem().getRegistryName().getPath();
  }

  private ResourceLocation itemTexture(Supplier<? extends IItemProvider> item) {
    return modLoc("item/" + name(item));
  }

  private ItemModelBuilder blockItem(Supplier<? extends Block> block) {
    return blockItem(block, "");
  }

  private ItemModelBuilder blockItem(Supplier<? extends Block> block, String suffix) {
    String name = name(block);
    return getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name + suffix)));
  }

  private ItemModelBuilder generated(Supplier<? extends IItemProvider> item) {
    return generated(item, itemTexture(item));
  }

  private ItemModelBuilder generated(Supplier<? extends IItemProvider> item, ResourceLocation texture) {
    return getBuilder(name(item)).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
  }

  private ItemModelBuilder handheld(Supplier<? extends IItemProvider> item) {
    return handheld(item, itemTexture(item));
  }

  private ItemModelBuilder handheld(Supplier<? extends IItemProvider> item, ResourceLocation texture) {
    return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
  }
}
