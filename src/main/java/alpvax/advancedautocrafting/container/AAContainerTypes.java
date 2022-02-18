package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AAContainerTypes {
  public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, AdvancedAutocrafting.MODID);

  public static final RegistryObject<MenuType<RemoteMasterContainer>> REMOTE_MASTER = CONTAINER_TYPES.register(
      "remote_master",
      AbstractTileEntityContainer.makeTypeSupplier(RemoteMasterContainer::new)
  );

  public static final RegistryObject<MenuType<ControllerContainer>> CONTROLLER = CONTAINER_TYPES.register(
      "controller",
      AbstractTileEntityContainer.makeTypeSupplier(ControllerContainer::new)
  );
}

