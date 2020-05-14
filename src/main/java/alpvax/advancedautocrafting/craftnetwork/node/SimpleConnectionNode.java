package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.UniversalPos;
import alpvax.advancedautocrafting.craftnetwork.connection.AdjacentConnector;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;

public class SimpleConnectionNode implements INetworkNode<Direction> {
  private final UniversalPos pos;
  private final EnumMap<Direction, AdjacentConnector> connectors = new EnumMap<>(Direction.class);

  public SimpleConnectionNode(UniversalPos pos) {
    this.pos = pos;
    for (Direction d : Direction.values()) {
      connectors.put(d, new AdjacentConnector.Simple(pos.offset(d)));
    }
  }

  @Nonnull
  @Override
  public UniversalPos getPos() {
    return pos;
  }

  @Nullable
  @Override
  public INodeConnector getConnector(Direction context) {
    return connectors.get(context);
  }
}
