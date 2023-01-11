package alpvax.advancedautocrafting.craftnetwork.chunk;

import alpvax.advancedautocrafting.craftnetwork.INetworkJunction;
import alpvax.advancedautocrafting.craftnetwork.INetworkSegment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChunkNetworkSegment implements INetworkSegment {
    private final Set<RelativeBlockPos> allNodes = new HashSet<>();
    private ChunkPos chunkPosition;
    private ServerLevel level;

    public void updateNode(final BlockPos blockPos) {
        RelativeBlockPos pos = new RelativeBlockPos(blockPos);
        Set<RelativeBlockPos> adjacent = pos.getAdjacentPositions()
            .filter(allNodes::contains)
            .collect(Collectors.toSet());

    }

    @Override
    public Collection<INetworkJunction> getJunctions() {
        return null;
    }

//    private enum SideConnectivity {
//        CONNECTED, DYNAMIC, DISABLED;
//    }
//    private record AdjacentNodeConnectivity ()

    /**
     * Local position within this chunk.
     * <p>
     * Comparison order is y -> x -> z (i.e. smallest is bottom layer, then (x = 0, z = 15) comes immediately before (x = 1, z = 0))
     */
    private class RelativeBlockPos {
        /**
         * x co-ord is highest 4 bits, z is lowest 4
         */
        private final byte xz;
        private final int y;

        private RelativeBlockPos(byte x, int y, byte z) {
            xz = (byte) (((x & 0xF) << 4) + (z & 0xF));
            this.y = y;
        }

        private RelativeBlockPos(int x, int y, int z) {
            xz = (byte) (((x & 0xF) << 4) + (z & 0xF));
            this.y = y;
        }

        private RelativeBlockPos(Vec3i pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        int getX() {
            return xz >> 4;
        }

        int getZ() {
            return xz & 0x0F;
        }

        int getY() {
            return y;
        }

        boolean isEdge() {
            int x = getX();
            int z = getZ();
            return x == 0 || x == 15 || z == 0 || z == 15 || y == level.getMinBuildHeight() ||
                   y == level.getMaxBuildHeight();
        }

        Stream<RelativeBlockPos> getAdjacentPositions() {
            int x = getX();
            int z = getZ();
            return Arrays.stream(Direction.values())
                .map(d -> new RelativeBlockPos(x + d.getStepX(), y + d.getStepY(), z + d.getStepZ()))
                .filter(p -> {
                    int px = p.getX();
                    int py = p.getY();
                    int pz = p.getZ();
                    return px >= 0 && px < 16 && pz >= 0 && pz < 16 && py >= level.getMinBuildHeight() &&
                           py <= level.getMaxBuildHeight();
                });
        }

        @Override
        public int hashCode() {
            return y << 8 + xz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RelativeBlockPos pos = (RelativeBlockPos) o;
            return xz == pos.xz && y == pos.y;
        }
    }
}
