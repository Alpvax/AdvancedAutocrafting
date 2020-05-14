package alpvax.advancedautocrafting.craftnetwork.connection;

import net.minecraft.util.Direction;

public class ConnectorContext {
  private Direction direction = null;
  public ConnectorContext direction(Direction d) {
    ConnectorContext ctx = new ConnectorContext();
    ctx.direction = d;
    return ctx;
  }
}
