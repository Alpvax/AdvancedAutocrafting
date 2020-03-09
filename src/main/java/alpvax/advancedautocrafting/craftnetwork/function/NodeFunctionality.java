package alpvax.advancedautocrafting.craftnetwork.function;

import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import com.google.common.collect.Maps;
import net.minecraft.util.NonNullList;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class NodeFunctionality<T> {
  private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case. And use _ to separate words.
  private static final Map<String, NodeFunctionality<?>> values = Maps.newHashMap();

  @SuppressWarnings("unchecked")
  public static <T> NodeFunctionality<T> get(String name)
  {
    if (VALID_NAME.matcher(name).find())
      throw new IllegalArgumentException("NodeFunctionality.of() called with invalid name: " + name);
    return (NodeFunctionality<T>) values.computeIfAbsent(name, NodeFunctionality::new);
  }

  public static <T> Chain chain(NodeFunctionality<T> functionality, Supplier<T> value) {
    return new Chain().chain(functionality, value);
  }
  public static final class Chain {
    private Map<NodeFunctionality<?>, Supplier<?>> values = new HashMap<>();
    public <T> Chain chain(NodeFunctionality<T> functionality, Supplier<T> value) {
      values.put(functionality, value);
      return this;
    }
    public <T> Optional<T> get(NodeFunctionality<T> functionality) {
      return Optional.ofNullable(values.get(functionality))
          .map(Supplier::get)
          .map(functionality::cast);
    }
  }

  private final String name;

  private NodeFunctionality(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  @SuppressWarnings("unchecked")
  public T cast(Object value) {
    return (T) value;
  }
  public Optional<T> value(NodeFunctionality<T> functionality, T value) {
    return functionality == this ? Optional.of(value) : Optional.empty();
  }

  public static final NodeFunctionality<NonNullList<INodeConnection<?>>> EXTENDED_CONNECT = get("extended_connect");
  public static final NodeFunctionality<IEnergyStorage> FORGE_ENERGY = get("forge_energy");

}
