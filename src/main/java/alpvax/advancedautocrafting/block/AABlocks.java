package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import alpvax.advancedautocrafting.block.tile.RemoteMasterTileEntity;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;


public class AABlocks {
  public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, AdvancedAutocrafting.MODID);
  public static final DeferredRegister<Item> ITEMS = AAItems.ITEMS;

  public static final RegistryObject<Block> CONTROLLER = register("controller", () -> new ControllerBlock(
      Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(6.0F, 30F)
  ));
  public static final RegistryObject<Block> REMOTE_MARKER = register("remote_marker", () -> new RemotePositionMarkerBlock(
      Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(1.0F, 30F)
  ));
  public static final RegistryObject<Block> REMOTE_MASTER = register("remote_master", () -> new RemoteMasterBlock(
      Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(6.0F, 30F)
  ));
  public static final RegistryObject<Block> WIRE = register("wire", () -> new WireBlock(
      Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(2.0F, 30F)
  ));

  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block) {
    return register(name, block, (b) -> () -> new BlockItem(b.get(), new Item.Properties().group(AdvancedAutocrafting.ITEM_GROUP)));
  }

  private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
    RegistryObject<T> ret = registerBlockOnly(name, block);
    ITEMS.register(name, itemCreator.apply(ret));
    return ret;
  }

  private static <T extends Block> RegistryObject<T> registerBlockOnly(String name, Supplier<? extends T> sup) {
    return BLOCKS.register(name, sup);
  }

  public static class TileTypes {
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, AdvancedAutocrafting.MODID);

    public static final RegistryObject<TileEntityType<ControllerTileEntity>> CONTROLLER = TILES.register("controller", () ->
        new TileEntityType<>(ControllerTileEntity::new, Sets.newHashSet(AABlocks.CONTROLLER.get()), null)
    );
    public static final RegistryObject<TileEntityType<RemoteMasterTileEntity>> REMOTE_MASTER = TILES.register("remote_master", () ->
        new TileEntityType<>(RemoteMasterTileEntity::new, Sets.newHashSet(AABlocks.REMOTE_MASTER.get()), null)
    );
  }
}
