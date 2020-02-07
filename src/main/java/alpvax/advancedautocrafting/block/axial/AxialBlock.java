package alpvax.advancedautocrafting.block.axial;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AxialBlock<T extends Comparable<T>> extends Block {
  public static final Direction[] ALL_DIRECTIONS = Direction.values();

  private static class CurrentBlockPropBuilder {
    private Map<Direction, IProperty<?>> propMap;
    private <T extends Comparable<T>> void make(Function<Direction, IProperty<T>> provider) {
      propMap = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
        for (Direction d : ALL_DIRECTIONS) {
          IProperty<T> prop = provider.apply(d);
          if (prop != null) {
            map.put(d, prop);
          }
        }
      });
    }
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> Map<Direction, IProperty<T>> get() {
      Map<Direction, IProperty<T>> map = Maps.newEnumMap(Direction.class);
      propMap.forEach((d, p) -> map.put(d, (IProperty<T>) p));
      propMap = null;
      return map;
    }
  }
  private static CurrentBlockPropBuilder currentPropBuilder = new CurrentBlockPropBuilder();
  private static <T extends Comparable<T>> Properties createBlockStateMap(Function<Direction, IProperty<T>> directionPropertyProvider, Properties properties) {
    currentPropBuilder.make(directionPropertyProvider);
    return properties;
  }

  private Map<Direction, IProperty<T>> directionToPropertyMap;
  protected final AxialBlockShape<T> shape;

  public AxialBlock(Properties properties, AxialBlockShape<T> shape, Function<Direction, IProperty<T>> directionPropertyProvider) {
    super(createBlockStateMap(directionPropertyProvider, properties));
    this.shape = shape;
  }

  public void forEachDirection(BiConsumer<Direction, IProperty<T>> consumer) {
    directionToPropertyMap.forEach(consumer);
  }

  public IProperty<T> getConnectionProp(Direction d) {
    return directionToPropertyMap.get(d);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    if(directionToPropertyMap == null) {
      directionToPropertyMap = currentPropBuilder.get();
    }
    for(IProperty<T> prop : directionToPropertyMap.values()) {
      builder.add(prop);
    }
  }

  public AxialBlockShape<T> getBlockShape() {
    return shape;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Map<Direction, T> values = Maps.newEnumMap(Direction.class);
    directionToPropertyMap.forEach((d, prop) -> {
      values.put(d, state.get(prop));
    });
    return shape.getCombinedShape(values);
  }

  @OnlyIn(Dist.CLIENT)
  public void buildBlockStateDefaults(Function<Block, MultiPartBlockStateBuilder> getBuilder, BlockModelProvider models) {
    String path = "block/part/" + getRegistryName().getPath() + "_";
    buildBlockState(
        getBuilder.apply(this),
        models.getBuilder(path + "core"),
        (d, f) -> f.uvs(0, 0, 16, 16),
        part -> models.getBuilder(path + part.name)
    );
  }

  @OnlyIn(Dist.CLIENT)
  public void buildBlockState(MultiPartBlockStateBuilder builder,
                              BlockModelBuilder coreModelBuilder,
                              BiConsumer<Direction, BlockModelBuilder.ElementBuilder.FaceBuilder> coreFaceMapper,
                              Function<AxialPart<T>, BlockModelBuilder> modelBuilderFactory
  ) {
    builder.part().modelFile(shape.buildCorePart(coreModelBuilder, coreFaceMapper)).addModel();
    directionToPropertyMap.forEach((dir, value) -> {
      int yrot = dir.getAxis().isHorizontal() ? (((int) dir.getHorizontalAngle()) + 180) % 360 : 0;
      int xrot = dir.getYOffset() == 0 ? 0 : dir.getYOffset() > 0 ? 270 : 90;
      shape.parts.values().forEach(part -> {
        builder.part()
            .modelFile(part.makeModelElement(modelBuilderFactory.apply(part)).end())
            .rotationY(yrot)
            .rotationX(xrot)
            .uvLock(true)
            .addModel()
            .condition(value, part.getAllowedValues());
      });
    });
  }
}
