package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.WireBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.Set;
import java.util.function.Supplier;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AABlockstateProvider extends BlockStateProvider {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

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

  protected void wireBlock(Supplier<Block> sup) {
    Block block = sup.get();
    String name = block.getRegistryName().getPath();
    wireBlock(block, "block/" + name + "_core", "block/" + name + "_connection", "block/" + name + "_interface", "block/" + name + "_disabled");
  }
  protected void wireBlock(Block block, String coreModel, String wireModel, String ifaceModel, String disabledModel) {
    float coreMin = 8 - WireBlock.Shape.CORE_RADIUS;
    float coreMax = 8 + WireBlock.Shape.CORE_RADIUS;
    wireBlock(
        block,
        models().getBuilder(coreModel).texture("texture", blockTexture(block)).texture("particle", "#texture").element()
            .from(coreMin, coreMin, coreMin)
            .to(coreMax, coreMax, coreMax)
            .allFaces((d, f) -> f.uvs(0, 0, 16, 16).texture("#texture"))
            .end(),
        edgeAxialPart(wireModel, WireBlock.Shape.WIRE_RADIUS, 8, Direction.SOUTH).end().texture("texture", blockTexture(block)),
        edgeAxialPart(ifaceModel, WireBlock.Shape.INTERFACE_RADIUS, WireBlock.Shape.INTERFACE_WIDTH)
            .face(Direction.SOUTH).uvs(0, 0, 16, 16).end().end().texture("texture", blockTexture(block)),
        axialPart(disabledModel, WireBlock.Shape.DISABLED_RADIUS, coreMin - WireBlock.Shape.DISABLED_WIDTH, coreMin, Direction.SOUTH)
            .face(Direction.NORTH).uvs(0, 0, 16, 16).end().end().texture("texture", blockTexture(block))
    );
  }

  private ModelBuilder.ElementBuilder edgeAxialPart(String name, float radius, float length, Direction... ignoredFaces) {
    ModelBuilder.ElementBuilder builder = axialPart(name, radius, 0, length, ignoredFaces);
    builder.face(Direction.NORTH).cullface(Direction.NORTH);
    return builder;
  }

  private ModelBuilder.ElementBuilder axialPart(String name, float radius, float start, float end, Direction... ignoredFaces) {
    Set<Direction> dirs = Set.of(ignoredFaces);
    ModelBuilder.ElementBuilder builder = models().getBuilder(name).texture("particle", "#texture").element()
        .from(8 - radius, 8 - radius, start)
        .to(8 + radius, 8 + radius, end);
    for(Direction d : ALL_DIRECTIONS) {
      if(dirs.contains(d)) continue;
      builder.face(d).texture("#texture");
    }
    return builder;
  }

  protected void wireBlock(Block block, ModelFile core, ModelFile wire, ModelFile iface, ModelFile disabled) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(core).addModel().end();
    WireBlock.DIR_TO_PROPERTY_MAP.entrySet().forEach(e -> {
      Direction dir = e.getKey();
      int yrot = dir.getAxis().isHorizontal() ? (((int) dir.getHorizontalAngle()) + 180) % 360 : 0;
      int xrot = dir.getYOffset() == 0 ? 0 : dir.getYOffset() > 0 ? 270 : 90;
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
      builder.part()
          .modelFile(disabled)
          .rotationY(yrot)
          .rotationX(xrot)
          .uvLock(true)
          .addModel()
          .condition(e.getValue(), WireBlock.ConnectionState.DISABLED);
    });
  }
}
