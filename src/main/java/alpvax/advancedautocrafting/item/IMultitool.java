package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class IMultitool {
  //TODO: Useful methods?

  public static class Provider implements ICapabilityProvider {
    private IMultitool multitool;

    Provider() {
      this(Capabilities.MULTITOOL_CAPABILITY::getDefaultInstance);
    }
    Provider(Supplier<IMultitool> sup) {
      multitool = sup.get();
    }

    private LazyOptional<IMultitool> capability = LazyOptional.of(() -> multitool);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == Capabilities.MULTITOOL_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }
  }
}
