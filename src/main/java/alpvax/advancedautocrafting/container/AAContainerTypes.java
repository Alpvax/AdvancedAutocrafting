package alpvax.advancedautocrafting.container;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AAContainerTypes {
  public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, AdvancedAutocrafting.MODID);

  public static final RegistryObject<ContainerType<RemoteMasterContainer>> REMOTE_MASTER = CONTAINER_TYPES.register(
      "remote_master",
      AbstractTileEntityContainer.makeTypeSupplier(RemoteMasterContainer::new)
  );

  public static final RegistryObject<ContainerType<ControllerContainer>> CONTROLLER = CONTAINER_TYPES.register(
      "controller",
      AbstractTileEntityContainer.makeTypeSupplier(ControllerContainer::new)
  );
}

