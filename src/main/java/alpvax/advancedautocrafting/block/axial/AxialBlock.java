package alpvax.advancedautocrafting.block.axial;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  private final AxialBlockShape<T> shape;

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

  @Nonnull
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    if(context instanceof EntitySelectionContext) {
      Entity e = ((EntitySelectionContext)context).getEntity();
      if (e != null) {
        if (context.hasItem(AAItems.MULTITOOL.get())
                || (e instanceof LivingEntity && ((LivingEntity)e).getActiveItemStack().getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent())
        ) {
          Vec3d start = new Vec3d(e.prevPosX, e.prevPosY + e.getEyeHeight(), e.prevPosZ);
          Vec3d end = start.add(e.getLook(0).scale(PlayerEntity.REACH_DISTANCE.clampValue(Double.MAX_VALUE)));
          return getPartialBlockHighlight(state, rayTracePart(state, pos, start, end));
        }
      }
    }
    Map<Direction, T> values = Maps.newEnumMap(Direction.class);
    directionToPropertyMap.forEach((d, prop) -> values.put(d, state.get(prop)));
    return shape.getCombinedShape(values);
  }

  public IAxialPartInstance<T> rayTracePart(BlockState state, BlockPos pos, Vec3d start, Vec3d end) {
    Direction dir = null;
    AxialPart<T> part = null;
    BlockRayTraceResult ray = shape.getCoreShape().rayTrace(start, end, pos);
    double dSquared = ray == null ? Double.MAX_VALUE : ray.getHitVec().squareDistanceTo(start);
    for (Direction d : ALL_DIRECTIONS) {
      T propValue = state.get(getConnectionProp(d));
      for (AxialPart<T> p : shape.validParts(propValue).collect(Collectors.toList())) {
        ray = p.getShape(d).rayTrace(start, end, pos);
        if (ray != null) {
          double d2 = ray.getHitVec().squareDistanceTo(start);
          if (d2 < dSquared) {
            dSquared = d2;
            dir = d;
            part = p;
          }
        }
      }
    }
    return dir == null ? IAxialPartInstance.AxialCore.from(shape) : new IAxialPartInstance.Impl<T>(part, dir);
  }

  protected VoxelShape getPartialBlockHighlight(BlockState state, IAxialPartInstance<T> partInstance) {
    return partInstance.shape();
  }
}
