package alpvax.advancedautocrafting.craftnetwork.node;

import alpvax.advancedautocrafting.craftnetwork.connection.AdjacentConnector;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnector;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public abstract class SimpleNetworkConnector {
  private EnumMap<Direction, INodeConnector> connectors = new EnumMap<>(Direction.class);

  public SimpleNetworkConnector(IWorld world, BlockPos pos) {
    for (Direction d : Direction.values()) {
      connectors.put(d, new AdjacentConnector(world, pos, d) {
        @Nonnull
        @Override
        public Connectivity getInboundConnectivity() {
          return Connectivity.ALLOW;
        }

        @Nonnull
        @Override
        public Connectivity getOutboundConnectivity() {
          return Connectivity.ALLOW;
        }
      });
    }
  }

  public EnumMap<Direction, INodeConnector> getConnectors(BlockState state, IWorldReader world, BlockPos pos) {
    return connectors;
  }
}
