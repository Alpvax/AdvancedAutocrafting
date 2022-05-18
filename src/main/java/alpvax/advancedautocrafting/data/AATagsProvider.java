package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.init.AAItems;
import alpvax.advancedautocrafting.init.AATags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public abstract class AATagsProvider {
    public static void addProviders(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        BlockTagsProvider blockTags = new BlockProvider(generator, existingFileHelper);
        generator.addProvider(blockTags);
        generator.addProvider(new ItemProvider(generator, blockTags, existingFileHelper));
    }

    private static class BlockProvider extends BlockTagsProvider {
        public BlockProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, AAReference.MODID, existingFileHelper);
        }

        @Override
        public void addTags() {
        }

        @Override
        public String getName() {
            return "AdvancedAutocrafting Block Tags";
        }
    }

    private static class ItemProvider extends ItemTagsProvider {
        protected ItemProvider(
            DataGenerator generator, BlockTagsProvider blockTags,
            @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, blockTags, AAReference.MODID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            ResourceKey<Item> multitool = itemKey(AAItems.MULTITOOL);
            //noinspection unchecked
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

        private ResourceKey<Item> itemKey(ResourceLocation loc) {
            return ResourceKey.create(Registry.ITEM_REGISTRY, loc);
        }

        @SuppressWarnings("SameParameterValue")
        private ResourceKey<Item> itemKey(RegistryObject<Item> obj) {
            return itemKey(obj.getId());
        }
    }
}
