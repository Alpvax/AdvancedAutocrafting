package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.init.AABlocks;
import alpvax.advancedautocrafting.init.PositionReferenceLootFunction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AALootTableProvider extends LootTableProvider {

    public AALootTableProvider(PackOutput packOutput) {
        super(packOutput, Collections.emptySet(), List.of(
            new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        // Nothing for now
    }

    private static class Blocks extends BlockLootSubProvider {

        protected Blocks() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }
        @Override
        protected void generate() {
            dropSelf(AABlocks.CONTROLLER);
            dropSelf(AABlocks.REMOTE_MASTER);
            add(
                AABlocks.POSITION_MARKER.get(),
                withPosition(AABlocks.POSITION_MARKER)
            );
            dropSelf(AABlocks.WIRE);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return AABlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
        }

        private void dropSelf(Supplier<? extends Block> block) {
            dropSelf(block.get());
        }

        @SuppressWarnings("SameParameterValue")
        private LootTable.Builder withPosition(Supplier<? extends ItemLike> item) {
            return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                              .add(LootItem.lootTableItem(item.get()).apply(PositionReferenceLootFunction.builder())));
        }
    }
}
