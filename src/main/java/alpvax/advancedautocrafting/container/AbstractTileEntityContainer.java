package alpvax.advancedautocrafting.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractTileEntityContainer<T extends BlockEntity> extends AbstractContainerMenu {
  public interface ITileEntityContainerFactory<T extends BlockEntity, C extends AbstractTileEntityContainer<T>> extends IContainerFactory<C> {
    C create(final int id, final Inventory inv, final T tile);

    @SuppressWarnings("unchecked")
    @Override
    default C create(int windowId, Inventory inv, FriendlyByteBuf data) {
      return create(windowId, inv, (T)Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos())));
    }
  }

  public static <T extends BlockEntity, C extends AbstractTileEntityContainer<T>> Supplier<MenuType<C>> makeTypeSupplier(
      final ITileEntityContainerFactory<T, C> factory
  ) {
    return () -> IForgeMenuType.create(factory);
  }

  private final T tileentity;

  public AbstractTileEntityContainer(MenuType<? extends AbstractTileEntityContainer<T>> type, final int id, final T tile) {
    super(type, id);
    tileentity = tile;
  }

  protected T getTileEntity() {
    return tileentity;
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return getTileEntity().getBlockPos().closerThan(
        playerIn.blockPosition(),
        playerIn.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + 1
    );
  }
}
