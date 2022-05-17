package alpvax.advancedautocrafting.api.craftnetwork;

import alpvax.advancedautocrafting.api.util.IPositionReference;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum NodeConnectivity {
    /** No connection can be formed */
    DISABLED,
    /**
     * Isolates a child-parent relationship.<br>
     * This connection can be connected to by parent networks,
     * but this network cannot detect the nodes of the connected network
     */
    INBOUND_ONLY,
    /**
     * Isolates a child-parent relationship.<br>
     * This connection can be connected to by child networks,
     * but the child network cannot detect the nodes of this network
     */
    OUTBOUND_ONLY,
    /** Standard connection between nodes on the same network */
    BIDIRECTIONAL;

    @FunctionalInterface
    public interface IBlockStateConnectivityMapper {
        NodeConnectivity getConnectivity(BlockState state, @Nullable Direction side, @Nonnull IPositionReference fromLocation);
    }
}
