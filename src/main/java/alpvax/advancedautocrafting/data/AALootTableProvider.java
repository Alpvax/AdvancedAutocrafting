package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
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
  @Nonnull
  @Override
  protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
    return ImmutableList.of(
        Pair.of(Blocks::new, LootParameterSets.BLOCK)
    );
  }

  @Override
  protected void validate(@Nonnull Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
    // Nothing for now
  }

  private static class Blocks extends BlockLootTables {

    @Override
    protected void addTables() {
      dropSelf(AABlocks.CONTROLLER);
      dropSelf(AABlocks.REMOTE_MASTER);
      add(AABlocks.REMOTE_MARKER.get(),
          (b) -> withPosition(b, AAItems.REMOTE_POS)/*LootPool.builder().addEntry(
              ItemLootEntry.builder(AAItems.REMOTE_POS.get())
                  .acceptFunction(BlockPosLootFunction.builder())
          )*/
      );
      dropSelf(AABlocks.WIRE);
    }

    private void dropSelf(Supplier<? extends Block> block) {
      dropSelf(block.get());
    }

    private LootTable.Builder withPosition(Block block, Supplier<? extends IItemProvider> item) {
      return LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(item.get()).apply(BlockPosLootFunction.builder())));
    }

    @Nonnull
    @Override
    protected Iterable<Block> getKnownBlocks() {
      return AABlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
    }
  }
}
