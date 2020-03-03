package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.WireBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.craftnetwork.connection.DirectNodeConnection;
import alpvax.advancedautocrafting.craftnetwork.connection.INodeConnection;
import alpvax.advancedautocrafting.craftnetwork.function.NodeFunctionality;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class WireNetworkNode implements INetworkNode {
  private final IWorldReader world;
  private final BlockPos pos;

  public WireNetworkNode(IWorldReader world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Override
  public int upkeepCost() {
    return 0;
  }

  @Nonnull
  @Override
  public NonNullList<DirectNodeConnection> getConnections() {
    NonNullList<DirectNodeConnection> list = NonNullList.create();
    BlockState state = world.getBlockState(getPos());
    if (state.getBlock() instanceof WireBlock) {
      ((WireBlock)state.getBlock()).forEachDirection((d, p) -> {
        switch (state.get(p)) {
          case INTERFACE:
            getAdjacentNode(d).ifPresent(node -> list.add(new DirectNodeConnection(this, node, d)));
            break;
          case CONNECTION:
            list.add(new DirectNodeConnection(this, new WireNetworkNode(world, getPos().offset(d))));
            break;
        }
      });
    }
    return list;
  }

  public Optional<INetworkNode> getAdjacentNode(Direction d) {
    BlockPos target = getPos().offset(d);
    BlockState state = world.getBlockState(target);
    if (state.hasTileEntity()) {
      LazyOptional<INetworkNode> cap = Objects.requireNonNull(world.getTileEntity(target)).getCapability(Capabilities.NODE_CAPABILITY, d.getOpposite());
      return Optional.ofNullable(cap.orElse(null));
    }
    return Optional.empty();
  }

  @Nonnull
  @Override
  public BlockPos getPos() {
    return null;
  }

  @Override
  public void connectionChanged() {

  }

  @Override
  public <T> Optional<T> getFunctionality(NodeFunctionality<T> functionality) {
    return Optional.empty();
  }
}
