package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.init.AABlocks;
import alpvax.advancedautocrafting.init.AAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Credits to the Tropicraft team, for their sample and helper methods.
 */
@SuppressWarnings("UnusedReturnValue")
public class AAItemModelProvider extends ItemModelProvider {

    public AAItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AAReference.MODID, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Advanced Autocrafting Item Models";
    }

    @Override
    protected void registerModels() {

        handheld(AAItems.MULTITOOL);

        // BLOCKS
        blockItem(AABlocks.CONTROLLER);
        blockItem(AABlocks.REMOTE_MASTER);
        //blockItem(AABlocks.WIRE, "_core");

        generated(AABlocks.POSITION_MARKER).override()
            .predicate(new ResourceLocation(AAReference.MODID, "position_dimension"), 2F)
            .model(getExistingFile(blockPath(AABlocks.POSITION_MARKER.getId())))
            .end();
    }


    @SuppressWarnings("ConstantConditions")
    private String name(Supplier<? extends ItemLike> item) {
        return item.get().asItem().getRegistryName().getPath();
    }

    private ResourceLocation itemTexture(Supplier<? extends ItemLike> item) {
        return modLoc(ITEM_FOLDER + "/" + name(item));
    }

    private ItemModelBuilder blockItem(RegistryObject<? extends Block> block) {
        ResourceLocation blockID = block.getId();
        return withExistingParent(blockID.getPath(), blockPath(blockID));
    }

    private ResourceLocation blockPath(ResourceLocation blockId) {
        return new ResourceLocation(blockId.getNamespace(), BLOCK_FOLDER + "/" + blockId.getPath());
    }

    @SuppressWarnings("SameParameterValue")
    private ItemModelBuilder generated(Supplier<? extends ItemLike> item) {
        return generated(item, itemTexture(item));
    }

    private ItemModelBuilder generated(Supplier<? extends ItemLike> item, ResourceLocation texture) {
        return withExistingParent(name(item), "item/generated").texture("layer0", texture);
    }

    @SuppressWarnings("SameParameterValue")
    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item) {
        return handheld(item, itemTexture(item));
    }

    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item, ResourceLocation texture) {
        return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
    }
}
