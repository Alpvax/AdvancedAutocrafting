package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.Sets;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;


public class AABlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedAutocrafting.MODID);
  public static final DeferredRegister<Item> ITEMS = AAItems.ITEMS;

  public static final RegistryObject<Block> CONTROLLER = register("controller", () -> new ControllerBlock(
      Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(6.0F, 30F)
  ));
  public static final RegistryObject<Block> REMOTE_MARKER = register("remote_marker", () -> new RemotePositionMarkerBlock(
      Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.0F, 30F)
  ));
  public static final RegistryObject<Block> REMOTE_MASTER = register("remote_master", () -> new RemoteMasterBlock(
      Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(6.0F, 30F)
  ));
  public static final RegistryObject<WireBlock> WIRE = register("wire", () -> new WireBlock(
      Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(2.0F, 30F)
  ));

  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block) {
    return register(name, block, (b) -> () -> new BlockItem(b.get(), new Item.Properties().tab(AAItems.ITEM_GROUP)));
  }

  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
    RegistryObject<T> ret = registerBlockOnly(name, block);
    ITEMS.register(name, itemCreator.apply(ret));
    return ret;
  }

  private static <T extends Block> RegistryObject<T> registerBlockOnly(String name, Supplier<? extends T> sup) {
    return BLOCKS.register(name, sup);
  }

  @SuppressWarnings("ConstantConditions")
  public static class TileTypes {
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AdvancedAutocrafting.MODID);

    public static final RegistryObject<BlockEntityType<ControllerTileEntity>> CONTROLLER = TILES.register("controller", () ->
        new BlockEntityType<>(ControllerTileEntity::new, Sets.newHashSet(AABlocks.CONTROLLER.get()), null)
    );
    public static final RegistryObject<BlockEntityType<RemoteMasterTileEntity>> REMOTE_MASTER = TILES.register("remote_master", () ->
        new BlockEntityType<>(RemoteMasterTileEntity::new, Sets.newHashSet(AABlocks.REMOTE_MASTER.get()), null)
    );
  }
}
