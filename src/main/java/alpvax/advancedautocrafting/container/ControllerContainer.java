package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.block.tile.ControllerTileEntity;
import alpvax.advancedautocrafting.container.util.BlockHolderMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ControllerContainer extends AbstractTileEntityContainer.Extended<ControllerTileEntity> {//implements IBlockHolderContainer {
  private final BlockHolderMap endpoints = new BlockHolderMap();

  public ControllerContainer(int id, PlayerInventory playerInventory, ControllerTileEntity tile) {
    super(AAContainerTypes.CONTROLLER.get(), id, tile);
    updateEndpoints();
  }

  @Override
  void readExtendedData(PacketBuffer buf) {
    endpoints.readFrom(buf, false);
  }

  private void updateEndpoints() {
    ControllerTileEntity tile = getTileEntity();
    if (!tile.getLevel().isClientSide) {
      endpoints.clear();
      tile.getNodePositions().forEach(endpoints::put);/*p ->
          endpoints.getOrCreate(p.getWorld().func_234923_W_().func_240901_a_(), p.getBlockPos()).setBlockState(p.getBlockState())
      );*/
    }
  }

  public BlockHolderMap getBlocks() {
    return endpoints;
  }

  /*@Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();

  }*/
}
