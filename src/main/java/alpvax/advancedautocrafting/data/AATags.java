package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AATags {
    public static void init() {
        Blocks.init();
        Items.init();
    }

    public static class Blocks {


        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(AdvancedAutocrafting.MODID, name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }

        private static void init() {
        }
    }

    public static class Items {
        public static final TagKey<Item> MULTITOOL = tag("multitool");
        public static final TagKey<Item> FORGE_WRENCHES = forgeTag("wrenches");
        public static final TagKey<Item> FORGE_TOOLS = forgeTag("tools");
        public static final TagKey<Item> FORGE_TOOLS_WRENCH = forgeTag("tools/wrench");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(AdvancedAutocrafting.MODID, name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }

        private static void init() {
        }
    }
}
