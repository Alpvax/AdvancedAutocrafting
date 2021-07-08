package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.craftnetwork.segment.NetworkSegment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ChunkNetProvider implements ICapabilitySerializable<CompoundNBT> {
  private final ChunkNetwork network;
  private final LazyOptional<INetworkSegmentProvider> internal;

  private final Map<Direction, LazyOptional<INetworkSegmentProvider>> sides = new EnumMap<>(Direction.class);


  public ChunkNetProvider(Chunk chunk) {
    network = new ChunkNetwork(chunk.getPos());
    internal = LazyOptional.of(() -> network);
    Direction.Plane.HORIZONTAL.stream()
        .forEach(side -> sides.put(side, LazyOptional.of(() -> new SidedAccessor(side))));
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return Capabilities.CHUNK_NET_CAPABILITY.orEmpty(cap, sides.getOrDefault(side, internal));
  }

  @Override
  public CompoundNBT serializeNBT() {
    return null; //TODO: Implement serialisation
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    //TODO: Implement serialisation
  }

  private class SidedAccessor implements INetworkSegmentProvider {
    private Direction side;
    private SidedAccessor(Direction side) {
      this.side = side;
    }

    @Override
    public Set<NetworkSegment> availableSegments() {
      return network.segmentsForSide(side);
    }
  }
}
