package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.WireBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.function.Supplier;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AABlockstateProvider extends BlockStateProvider {

  public AABlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
    super(gen, AdvancedAutocrafting.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlock(CONTROLLER);
    simpleBlock(REMOTE_MARKER);
    simpleBlock(REMOTE_MASTER);
    wireBlock(WIRE);
  }

  private void simpleBlock(Supplier<Block> sup) {
    simpleBlock(sup.get());
  }

  protected void wireBlock(Supplier<Block> sup, ModelFile core, ModelFile wire, ModelFile iface) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(sup.get()).part().modelFile(core).addModel().end();
    WireBlock.DIR_TO_PROPERTY_MAP.entrySet().forEach(e -> {
      Direction dir = e.getKey();
      int yrot = (((int) dir.getHorizontalAngle()) + 180) % 360;
      int xrot = dir.getXOffset() == 0 ? 0 : dir.getXOffset() < 0 ? 270 : 90;
      builder.part()
              .modelFile(wire)
              .rotationY(yrot)
              .rotationX(xrot)
              .uvLock(true)
              .addModel()
              .condition(e.getValue(),
                      WireBlock.ConnectionState.CONNECTION,
                      WireBlock.ConnectionState.INTERFACE
              );
      builder.part()
              .modelFile(iface)
              .rotationY(yrot)
              .rotationX(xrot)
              .uvLock(true)
              .addModel()
              .condition(e.getValue(), WireBlock.ConnectionState.INTERFACE);
    });
  }
}
