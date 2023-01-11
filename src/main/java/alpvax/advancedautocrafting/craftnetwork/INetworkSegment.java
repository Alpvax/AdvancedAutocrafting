package alpvax.advancedautocrafting.craftnetwork;

import java.util.Collection;

public interface INetworkSegment<T> {
    Collection<INetworkJunction<T>> getJunctions();
}
