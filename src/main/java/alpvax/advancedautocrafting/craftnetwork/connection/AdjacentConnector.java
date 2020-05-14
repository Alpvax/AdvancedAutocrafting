package alpvax.advancedautocrafting.craftnetwork.connection;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class AdjacentConnector implements INodeConnector {
  private final UniversalPos target;
  public AdjacentConnector(IWorld world, BlockPos to) {
    target = new UniversalPos(world, to);
  }
  public AdjacentConnector(IWorld world, BlockPos from, Direction d) {
    target = new UniversalPos(world, from.offset(d));
  }
  public AdjacentConnector(UniversalPos from, Direction d) {
    this(from.offset(d));
  }
  public AdjacentConnector(UniversalPos to) {
    target = to;
  }

  @Nonnull
  @Override
  public UniversalPos getTargetPos() {
    return target;
  }

  @Nonnull
  @Override
  public Optional<INodeConnector> getTargetConnector() {
    //TODO: get node at position
    return Optional.empty();
  }

  public static class Simple extends AdjacentConnector {

    private boolean disabledInbound = false;
    private boolean disabledOutbound = false;

    public Simple(UniversalPos to) {
      super(to);
    }

    @Nonnull
    @Override
    public Connectivity getInboundConnectivity() {
      return disabledInbound ? Connectivity.BLOCK : Connectivity.ALLOW;
    }

    @Nonnull
    @Override
    public Connectivity getOutboundConnectivity() {
      return disabledOutbound ? Connectivity.BLOCK : Connectivity.ALLOW;
    }

    public Simple disableInbound(boolean disable) {
      disabledInbound = disable;
      return this;
    }
    public Simple disableOutbound(boolean disable) {
      disabledOutbound = disable;
      return this;
    }
  }
}
