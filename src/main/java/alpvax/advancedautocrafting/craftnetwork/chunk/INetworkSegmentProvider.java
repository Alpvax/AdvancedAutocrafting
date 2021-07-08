package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.craftnetwork.segment.NetworkSegment;

import java.util.Set;

public interface INetworkSegmentProvider {
  Set<NetworkSegment> availableSegments();
}
