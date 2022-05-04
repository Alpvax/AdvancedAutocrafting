package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AALootTableProvider extends LootTableProvider {

    public AALootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(
            Pair.of(Blocks::new, LootContextParamSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        // Nothing for now
    }

    private static class Blocks extends BlockLoot {

        @Override
        protected void addTables() {
            dropSelf(AABlocks.CONTROLLER);
            dropSelf(AABlocks.REMOTE_MASTER);
            add(AABlocks.REMOTE_MARKER.get(),
                (b) -> withPosition(b, AAItems.REMOTE_POS)
            );
            dropSelf(AABlocks.WIRE);
        }

        private void dropSelf(Supplier<? extends Block> block) {
            dropSelf(block.get());
        }

        @SuppressWarnings("SameParameterValue")
        private LootTable.Builder withPosition(Block block, Supplier<? extends ItemLike> item) {
            return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(item.get()).apply(BlockPosLootFunction.builder())));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return AABlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
        }
    }
}
