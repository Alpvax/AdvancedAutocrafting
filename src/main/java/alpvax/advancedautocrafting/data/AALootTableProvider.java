package alpvax.advancedautocrafting.data;

import alpvax.advancedautocrafting.block.AABlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTable.Builder;
import net.minecraft.world.storage.loot.ValidationTracker;

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
  protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
    return ImmutableList.of(
        Pair.of(Blocks::new, LootParameterSets.BLOCK)
    );
  }

  @Override
  protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
    // Nothing for now
  }

  private static class Blocks extends BlockLootTables {

    @Override
    protected void addTables() {
      dropsSelf(AABlocks.CONTROLLER);
    }

    private void dropsSelf(Supplier<? extends Block> block) {
      registerDropSelfLootTable(block.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
      return AABlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
    }
  }
}
