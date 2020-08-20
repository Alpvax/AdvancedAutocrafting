package alpvax.advancedautocrafting.container.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BlockHolderMap{
  private final Map<ResourceLocation, Map<BlockPos, ContainerBlockHolder>> map = new HashMap<>();

  public Optional<ContainerBlockHolder> put(@Nonnull ContainerBlockHolder holder) {
    return put(holder.getWorldID(), holder.getPos(), holder);
  }

  public Optional<ContainerBlockHolder> put(@Nonnull ResourceLocation dimension, @Nonnull BlockPos pos, @Nonnull ContainerBlockHolder holder) {
    return Optional.ofNullable(getOrCreateForDim(dimension).put(pos, holder));
  }

  public Optional<ContainerBlockHolder> get(ResourceLocation dimension, BlockPos pos) {
    return getDimensionMap(dimension).map(m -> m.get(pos));
  }

  private Map<BlockPos, ContainerBlockHolder> getOrCreateForDim(ResourceLocation dimension) {
    return map.computeIfAbsent(dimension, d -> new HashMap<>());
  }
  private Optional<Map<BlockPos, ContainerBlockHolder>> getDimensionMap(ResourceLocation dimension) {
    return Optional.ofNullable(map.get(dimension));
  }

  public ContainerBlockHolder getOrCreate(ResourceLocation dimension, BlockPos pos) {
    return getOrCreateForDim(dimension).computeIfAbsent(pos, p -> new ContainerBlockHolder(pos, dimension));
  }

  public boolean containsKey(ResourceLocation dimension) {
    return map.containsKey(dimension);
  }
  public boolean containsKey(ResourceLocation dimension, BlockPos pos) {
    return getDimensionMap(dimension).map(m -> m.containsKey(pos)).orElse(false);
  }

  public boolean containsValue(ContainerBlockHolder holder) {
    return get(holder.getWorldID(), holder.getPos()).filter(h -> h == holder).isPresent();
  }

  public int size() {
    return map.values().stream().mapToInt(Map::size).sum();
  }

  public boolean isEmpty() {
    return map.values().stream().allMatch(Map::isEmpty);
  }

  public void clear() {
    map.values().forEach(Map::clear);
    map.clear();
  }
  public void clear(ResourceLocation dimension) {
    getDimensionMap(dimension).ifPresent(Map::clear);
  }

  public Stream<ContainerBlockHolder> stream() {
    return map.values().stream().flatMap(m -> m.values().stream());
  }

  public Collection<ContainerBlockHolder> values() {
    return stream().collect(Collectors.toList());
  }

  public void forEach(Consumer<ContainerBlockHolder> action) {
    stream().forEach(action);
  }

  public Optional<ContainerBlockHolder> remove(ResourceLocation dimension, BlockPos pos) {
    return getDimensionMap(dimension).map(m -> m.remove(pos));
  }

  public boolean remove(ContainerBlockHolder holder) {
    return getDimensionMap(holder.getWorldID()).map(m -> m.remove(holder.getPos(), holder)).orElse(false);
  }

  public void readFrom(PacketBuffer buf, boolean clearFirst) {
    if (clearFirst) {
      clear();
    }
    int n = buf.readVarInt();
    for (int i = 0; i < n; i++) {
      put(ContainerBlockHolder.from(buf));
    }
  }
}
