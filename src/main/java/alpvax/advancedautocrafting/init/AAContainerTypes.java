package alpvax.advancedautocrafting.init;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.container.AbstractBlockEntityContainer;
import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.container.RemoteMasterContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AAContainerTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(
        ForgeRegistries.MENU_TYPES, AAReference.MODID);

    public static final RegistryObject<MenuType<RemoteMasterContainer>> REMOTE_MASTER = MENU_TYPES.register(
        "remote_master",
        AbstractBlockEntityContainer.makeTypeSupplier(RemoteMasterContainer::new)
    );

    public static final RegistryObject<MenuType<ControllerContainer>> CONTROLLER = MENU_TYPES.register(
        "controller",
        AbstractBlockEntityContainer.makeTypeSupplier(ControllerContainer::new)
    );
}

