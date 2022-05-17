package alpvax.advancedautocrafting.craftnetwork;


import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;


public class SimpleNetworkNode implements INetworkNode {
    private final BlockPos pos;

    public SimpleNetworkNode(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public NonNullList<INetworkNode> getChildNodes(Direction inbound) {
        return NonNullList.create();
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }
}
