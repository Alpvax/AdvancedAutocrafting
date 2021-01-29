package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.block.ConnectionState;
import alpvax.advancedautocrafting.block.tile.WireTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class WireConnectionsOptional { //<T extends IStringSerializable> {
  private final BlockPos pos;
  @Nullable
  final Map<Direction, ConnectionState> connections; //T> connections;
  @Nullable
  Pair<VoxelShape, Direction> closestRayTrace;

  public WireConnectionsOptional(@Nonnull IBlockReader world, @Nonnull BlockPos pos) {
    this.pos = pos;
    WireTileEntity t = AABlocks.TileTypes.WIRE.get().getIfExists(world, pos);
    if (t != null) {
      connections = new EnumMap<>(Direction.class);
      for (Direction d : Direction.values()) {
        connections.put(d, t.getConnection(d));
      }
    } else {
      connections = null;
    }
  }

  public boolean isPresent() {
    return connections != null;
  }

  //public WireConnectionsOptional partialRayTrace(WireShape<T> shape, Supplier<Entity> sup) {
  public WireConnectionsOptional partialRayTrace(WireShape<ConnectionState> shape, Supplier<Entity> sup) {
    if (connections != null) {
      closestRayTrace = Optional.ofNullable(sup.get())
                            .map(e -> e instanceof LivingEntity ? (LivingEntity) e : null)
                            .map(e -> {
                              ModifiableAttributeInstance att = e.getAttribute(ForgeMod.REACH_DISTANCE.get());
                              if (att == null) {
                                return null;
                              }
                              Vector3d start = e.getEyePosition(0);
                              return shape.rayTracePart(connections, pos, start, start.add(e.getLook(0).scale(att.getValue())));
                            })
                            .orElse(null);
    }
    return this;
  }

  public Optional<Map<Direction, ConnectionState>> getConnections() {
    return Optional.ofNullable(connections);
  }

  public Optional<VoxelShape> getShape() {
    return Optional.ofNullable(closestRayTrace).map(Pair::getLeft);
  }

  public Optional<Direction> getDirection() {
    return Optional.ofNullable(closestRayTrace).map(Pair::getRight);
  }

  public <T> Optional<T> map(Function<Map<Direction, ConnectionState>, T> connectionMapper) {
    return getConnections().map(connectionMapper);
  }

  public Optional<VoxelShape> getShapeOrMap(Function<Map<Direction, ConnectionState>, VoxelShape> connectionMapper) {
    Optional<VoxelShape> closest = getShape();
    if (closest.isPresent()) {
      return closest;
    }
    return map(connectionMapper);
  }

  public Optional<Direction> getDirectionOrMap(Function<Map<Direction, ConnectionState>, Direction> connectionMapper) {
    Optional<Direction> closest = getDirection();
    if (closest.isPresent()) {
      return closest;
    }
    return map(connectionMapper);
  }
}
