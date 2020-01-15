package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.AABlocks;
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

    // BLOCKS
    blockItem(AABlocks.CONTROLLER);
    blockItem(AABlocks.REMOTE_MARKER);
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
    return withExistingParent(name(block), modLoc("block/" + name(block) + suffix));
  }

  private ItemModelBuilder blockWithInventoryModel(Supplier<? extends Block> block) {
    return withExistingParent(name(block), modLoc("block/" + name(block) + "_inventory"));
  }

  private ItemModelBuilder blockSprite(Supplier<? extends Block> block) {
    return blockSprite(block, modLoc("block/" + name(block)));
  }

  private ItemModelBuilder blockSprite(Supplier<? extends Block> block, ResourceLocation texture) {
    return generated(() -> block.get().asItem(), texture);
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
