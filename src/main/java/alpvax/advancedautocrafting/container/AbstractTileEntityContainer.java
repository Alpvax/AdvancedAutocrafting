package alpvax.advancedautocrafting.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.function.Supplier;

public abstract class AbstractTileEntityContainer<T extends TileEntity> extends Container {
  public interface ITileEntityContainerFactory<T extends TileEntity, C extends AbstractTileEntityContainer<T>> extends IContainerFactory<C> {
    C create(final int id, final PlayerInventory inv, final T tile);

    @Override
    default C create(int windowId, PlayerInventory inv, PacketBuffer data) {
      return create(windowId, inv, (T)inv.player.world.getTileEntity(data.readBlockPos()));
    }
  }

  public static <T extends TileEntity, C extends AbstractTileEntityContainer<T>> Supplier<ContainerType<C>> makeTypeSupplier(
      final ITileEntityContainerFactory<T, C> factory
  ) {
    return () -> IForgeContainerType.create(factory);
  }

  private final T tileentity;

  public AbstractTileEntityContainer(ContainerType<? extends AbstractTileEntityContainer<T>> type, final int id, final T tile) {
    super(type, id);
    tileentity = tile;
  }

  protected T getTileEntity() {
    return tileentity;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return playerIn.getPosition().withinDistance(
        getTileEntity().getPos(),
        playerIn.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1
    );
  }
}
