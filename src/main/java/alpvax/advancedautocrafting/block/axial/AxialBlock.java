package alpvax.advancedautocrafting.block.axial;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.item.AAItems;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AxialBlock<T extends Comparable<T>> extends Block {
  public static final Direction[] ALL_DIRECTIONS = Direction.values();

  private Map<Direction, Property<T>> directionToPropertyMap;
  private final AxialBlockShape<T> shape;

  public AxialBlock(Properties properties, AxialBlockShape<T> shape) {
    super(properties);
    this.shape = shape;
    BlockState state = getDefaultState();
    for (Direction d : ALL_DIRECTIONS) {
      Property<T> prop = directionToPropertyMap.get(d);
      if (prop != null) {
        state = state.with(prop, getDefaultPropertyValue(d));
      }
    }
    setDefaultState(state);
  }

  public void forEachDirection(BiConsumer<Direction, Property<T>> consumer) {
    directionToPropertyMap.forEach(consumer);
  }

  @Nonnull
  public Optional<Property<T>> getConnectionProp(Direction d) {
    return d != null ? Optional.ofNullable(directionToPropertyMap.get(d)) : Optional.empty();
  }
  @Nonnull
  public Optional<T> getConnection(BlockState state, Direction d) {
    return getConnectionProp(d).map(prop -> state.hasProperty(prop) ? state.get(prop) : null);
  }

  @Override
  protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
    directionToPropertyMap = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
      for (Direction d : ALL_DIRECTIONS) {
        Property<T> prop = buildPropertyForDirection(d);
        if (prop != null) {
          map.put(d, prop);
          builder.add(prop);
        }
      }
    });
  }

  @Nullable
  protected abstract Property<T> buildPropertyForDirection(Direction d);

  @Nonnull
  protected abstract T getDefaultPropertyValue(Direction d);

  public AxialBlockShape<T> getBlockShape() {
    return shape;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
    if(context instanceof EntitySelectionContext) {
      Entity e = context.getEntity();
      if (e != null) {
        if (context.hasItem(AAItems.MULTITOOL.get())
                || (e instanceof LivingEntity && ((LivingEntity)e).getActiveItemStack().getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent())
        ) {
          Vector3d start = new Vector3d(e.prevPosX, e.prevPosY + e.getEyeHeight(), e.prevPosZ);
          Vector3d end = start.add(e.getLook(0).scale(ForgeMod.REACH_DISTANCE.get().clampValue(Double.MAX_VALUE)));
          return getPartialBlockHighlight(state, rayTracePart(state, pos, start, end));
        }
      }
    }
    Map<Direction, T> values = Maps.newEnumMap(Direction.class);
    directionToPropertyMap.forEach((d, prop) -> values.put(d, state.get(prop)));
    return shape.getCombinedShape(values);
  }

  public IAxialPartInstance<T> rayTracePart(BlockState state, BlockPos pos, Vector3d start, Vector3d end) {
    Direction dir = null;
    AxialPart<T> part = null;
    BlockRayTraceResult ray = shape.getCoreShape().rayTrace(start, end, pos);
    double dSquared = ray == null ? Double.MAX_VALUE : ray.getHitVec().squareDistanceTo(start);
    for (Direction d : ALL_DIRECTIONS) {
      Optional<Property<T>> prop = getConnectionProp(d);
      if (prop.isPresent()) {
        for (AxialPart<T> p : shape.validParts(state.get(prop.get())).collect(Collectors.toList())) {
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
    }
    return dir == null ? IAxialPartInstance.AxialCore.from(shape) : new IAxialPartInstance.Impl<>(part, dir);
  }

  protected VoxelShape getPartialBlockHighlight(BlockState state, IAxialPartInstance<T> partInstance) {
    Direction d = partInstance.direction();
    return getConnection(state, d).map(val -> getBlockShape().getAxialShape(d, val)).orElse(getBlockShape().getCoreShape());
    //return partInstance.shape();
  }
}
