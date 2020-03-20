package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractNetworkNode implements INetworkNode {
  private static final Direction[] ALL_DIRECTIONS = Direction.values();

  protected Supplier<IWorldReader> worldSup;
  protected Supplier<BlockPos> posSup;
  protected NodeFunctionality.Chain functionalities;
  //protected EnumMap<Direction, Function<AbstractNetworkNode, Connectivity>> connectivityMap;
  protected CraftNetwork currentNetwork = null;

  public AbstractNetworkNode(Supplier<IWorldReader> worldSup, Supplier<BlockPos> posSup) {
    this.worldSup = worldSup;
    this.posSup = posSup;
    functionalities = new NodeFunctionality.Chain();
    //connectivityMap = new EnumMap<>(Direction.class);
  }
  public AbstractNetworkNode(TileEntity tileEntity) {
    this(tileEntity::getWorld, tileEntity::getPos);
  }
  public AbstractNetworkNode(IWorldReader world, BlockPos pos) {
    this(() -> world, () -> pos);
  }

  public static class Builder extends AbstractNetworkNode {
    public Builder(Supplier<IWorldReader> worldSup, Supplier<BlockPos> posSup) {
      super(worldSup, posSup);
    }
    public Builder(TileEntity tileEntity) {
      super(tileEntity);
    }
    public Builder(IWorldReader world, BlockPos pos) {
      super(world, pos);
    }
    public Builder withFunctionalities(NodeFunctionality.Chain functionalities) {
      this.functionalities = functionalities;
      return this;
    }
    public <T> Builder withFunctionality(NodeFunctionality<T>functionality, Function<INetworkNode, T> factory) {
      functionalities.chain(functionality, factory);
      return this;
    }

    /**
     * Does not override existing values!
     *
    public Builder withConnectivity(Connectivity allSides) {
      for (Direction dir : ALL_DIRECTIONS) {
        connectivityMap.computeIfAbsent(dir, d -> n -> allSides);
      }
      return this;
    }
    /**
     * Overrides existing value!
     *
    public Builder withConnectivity(Direction d, Connectivity connectivity) {
      connectivityMap.put(d, n -> connectivity);
      return this;
    }
    /**
     * Overrides all values to use mapper function instead!
     *
    public Builder withConnectivity(BiFunction<Direction, AbstractNetworkNode, Connectivity> connectivityMapper) {
      for (Direction dir : ALL_DIRECTIONS) {
        connectivityMap.put(dir, n -> connectivityMapper.apply(dir, n));
      }
      return this;
    }*/
  }

  /*@Nonnull
  @Override
  public Connectivity getConnectivity(Direction dir) {
    return connectivityMap.get(dir).apply(this);
  }*/

  /*@Nonnull
  @Override
  public AdjacentNodeConnectionManager createConnectionManager() {
    return new AdjacentNodeConnectionManager(this);
  }*/

  @Nonnull
  @Override
  public IWorldReader getWorld() {
    return worldSup.get();
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return posSup.get();
  }

  @Nonnull
  @Override
  public Set<NodeFunctionality<?>> getFunctionalities() {
    return functionalities.handled();
  }

  @Nonnull
  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return functionalities.get(functionality, this);
  }

  @Override
  public void onNetworkConnect(CraftNetwork network) {
    this.currentNetwork = network;
  }

  @Override
  public void onNetworkDisconnect(CraftNetwork network) {
    this.currentNetwork = null;
  }

  @Nullable
  @Override
  public CraftNetwork getNetwork() {
    return currentNetwork;
  }
}
