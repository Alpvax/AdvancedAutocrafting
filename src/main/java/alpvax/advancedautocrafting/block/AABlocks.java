package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
      Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(6.0F, 30F)
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
}
