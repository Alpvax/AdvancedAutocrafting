package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.craftnetwork.segment.NetworkSegment;

import java.util.Set;

public interface INetworkSegmentProvider {
  Set<NetworkSegment> availableSegments();

  class Wrapped implements INetworkSegmentProvider {
    private Set<NetworkSegment> segments;
    Wrapped(Set<NetworkSegment> segments) {
      this.segments = segments;
    }

    @Override
    public Set<NetworkSegment> availableSegments() {
      return segments;
    }
  }
}
