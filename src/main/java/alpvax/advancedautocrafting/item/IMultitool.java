package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IMultitool {
  //TODO: Useful methods?

  class Provider implements ICapabilityProvider {
    private Supplier<IMultitool> multitoolSup;

    Provider() {
      this(() -> new IMultitool() {});
    }
    Provider(Supplier<IMultitool> sup) {
      multitoolSup = sup;
    }

    private final LazyOptional<IMultitool> capability = LazyOptional.of(() -> multitoolSup.get());

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == Capabilities.MULTITOOL_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }
  }
}
