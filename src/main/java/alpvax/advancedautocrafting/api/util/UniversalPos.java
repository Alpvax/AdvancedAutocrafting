package alpvax.advancedautocrafting.api.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

public class UniversalPos implements Comparable<UniversalPos>, ITileEntityProvider<World> {
  private final IWorld world;
  private final BlockPos pos;

  public static UniversalPos from(IWorldPosCallable c) {
    return c.apply(UniversalPos::new).orElseThrow(() -> new NullPointerException("Failed to create UniversalPos from IWorldPosCallable: " + c.toString()));
  }
  public UniversalPos(@Nonnull IWorld world, @Nonnull BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  public boolean isLoaded() {
    return this.isLoaded(0);
  }
  public boolean isLoaded(int adjacentBlocks) {
    return getWorld().isAreaLoaded(getPos(), adjacentBlocks);
  }

  /*TODO:public <T> Optional<T> ifCraftNetNode(Function<INetworkNode, T> callback, boolean load) {
    return getWorld().getCapability(Capabilities.NETWORK_GRAPH_CAPABILITY).map(graph ->
                                                                                   graph.getNode(getPos()).map(callback)
    ).orElseThrow(() -> new NullPointerException("World %s did not have network capability attached"));
  }*/

  @Nonnull
  public World getWorld() {
    return world.getWorld();
  }

  @Nonnull
  public BlockPos getPos() {
    return pos;
  }

  public BlockState getState() {
    return getWorld().getBlockState(getPos());
  }

  /**
   * Equivalent of {@link BlockPos#offset}
   * @return a new UniversalPos with the same world, and the offset pos
   */
  public UniversalPos offset(Direction d) {
    return new UniversalPos(world, pos.offset(d));
  }

  public ITextComponent singleLineDisplay() {
    return new StringTextComponent("Dimension: \"" + DimensionType.getKey(getWorld().getDimension().getType()).toString()
                                       + "\"; Position: " + getPos() + ";");//TODO: Convert to translation?
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, new ChunkPos(pos), pos);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof UniversalPos) {
      UniversalPos o = (UniversalPos) obj;
      return world.equals(o.world) && pos.equals(o.pos);
    }
    return false;
  }

  @Override
  public int compareTo(@Nonnull UniversalPos o) {
    return Comparator.<UniversalPos>comparingInt(up -> up.getWorld().getDimension().getType().getId())
               .thenComparing(UniversalPos::getPos).compare(this, o);
  }
}