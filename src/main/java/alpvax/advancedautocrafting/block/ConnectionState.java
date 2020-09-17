package alpvax.advancedautocrafting.block;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Optional;

public enum ConnectionState implements IStringSerializable {
  NONE,
  CONNECTION,
  INTERFACE,
  DISABLED;

  private static final ConnectionState[] VALUES = values();
  private final String name;

  ConnectionState() {
    name = name().toLowerCase(Locale.ENGLISH);
  }

  public boolean isNotDisabled() {
    return this != DISABLED;
  }

  @Nonnull
  @Override //getName
  public String func_176610_l() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static Optional<ConnectionState> get(String name) {
    for (ConnectionState s : VALUES) {
      if (name.equals(s.func_176610_l()) || name.equalsIgnoreCase(s.name())) {
        return Optional.of(s);
      }
    }
    return Optional.empty();
  }
}
