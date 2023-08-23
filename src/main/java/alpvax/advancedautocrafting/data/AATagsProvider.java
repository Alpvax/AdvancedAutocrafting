package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.init.AAItems;
import alpvax.advancedautocrafting.init.AATags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public abstract class AATagsProvider {
    public static void addProviders(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, boolean includeServer, ExistingFileHelper existingFileHelper) {
        PackOutput output = generator.getPackOutput();
        BlockTagsProvider blockTags = new BlockProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(includeServer, blockTags);
        generator.addProvider(includeServer, new ItemProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    }

    private static class BlockProvider extends BlockTagsProvider {
        public BlockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, AAReference.MODID, existingFileHelper);
        }

        @Override
        public void addTags(HolderLookup.Provider provider) {
        }

        @Override
        public String getName() {
            return "AdvancedAutocrafting Block Tags";
        }
    }

    private static class ItemProvider extends ItemTagsProvider {
        protected ItemProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTags, AAReference.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            ResourceKey<Item> multitool = AAItems.MULTITOOL.getKey();
            //noinspection unchecked,ConstantConditions
            tag(AATags.Items.MULTITOOL).add(multitool)
                .addTags(AATags.Items.FORGE_TOOLS_WRENCH, AATags.Items.FORGE_WRENCHES);
            tag(AATags.Items.FORGE_TOOLS).addTag(AATags.Items.MULTITOOL);
            tag(AATags.Items.FORGE_TOOLS_WRENCH).add(multitool);
            tag(AATags.Items.FORGE_WRENCHES).add(multitool);
        }

        @Override
        public String getName() {
            return "AdvancedAutocrafting Item Tags";
        }
    }
}
