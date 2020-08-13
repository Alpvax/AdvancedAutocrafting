package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerContainer extends AbstractTileEntityContainer<ControllerTileEntity> {
  private List<ProxyBlockSource> nodes;

  public ControllerContainer(int id, PlayerInventory playerInventory, ControllerTileEntity tile) {
    super(AAContainerTypes.CONTROLLER.get(), id, tile);
    updateNodes();
  }

  public void updateNodes() {
    //noinspection ConstantConditions
    nodes = getTileEntity().updateAdjacentNetwork().stream()
                .map(n -> new ProxyBlockSource(getTileEntity().getWorld(), n.getPos()))
                .collect(Collectors.toList());
  }

  public List<BlockState> getStates() {
    return nodes.subList(0, Math.min(5, nodes.size())).stream().map(ProxyBlockSource::getBlockState).collect(Collectors.toList());
  }
}
