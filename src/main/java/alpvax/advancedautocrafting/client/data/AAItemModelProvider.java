package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Credits to the Tropicraft team, for their sample and helper methods.
 */
public class AAItemModelProvider extends ItemModelProvider {

    public AAItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AdvancedAutocrafting.MODID, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Advanced Autocrafting Item Models";
    }

    @Override
    protected void registerModels() {

        generated(AAItems.POSITION_MARKER).override()
            .predicate(new ResourceLocation(AdvancedAutocrafting.MODID, "position_dimension"), 0)
            .model(new ModelFile.UncheckedModelFile(modLoc("block/" + name(AABlocks.POSITION_MARKER))));
        handheld(AAItems.MULTITOOL);

        // BLOCKS
        blockItem(AABlocks.CONTROLLER);
        blockItem(AABlocks.REMOTE_MASTER);
        //blockItem(AABlocks.WIRE, "_core");
    }


    @SuppressWarnings("ConstantConditions")
    private String name(Supplier<? extends ItemLike> item) {
        return item.get().asItem().getRegistryName().getPath();
    }

    private ResourceLocation itemTexture(Supplier<? extends ItemLike> item) {
        return modLoc("item/" + name(item));
    }

    private ItemModelBuilder blockItem(Supplier<? extends Block> block) {
        return blockItem(block, "");
    }

    private ItemModelBuilder blockItem(Supplier<? extends Block> block, String suffix) {
        String name = name(block);
        return getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name + suffix)));
    }

    private ItemModelBuilder generated(Supplier<? extends ItemLike> item) {
        return generated(item, itemTexture(item));
    }

    private ItemModelBuilder generated(Supplier<? extends ItemLike> item, ResourceLocation texture) {
        return getBuilder(name(item)).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item) {
        return handheld(item, itemTexture(item));
    }

    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item, ResourceLocation texture) {
        return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
    }
}
