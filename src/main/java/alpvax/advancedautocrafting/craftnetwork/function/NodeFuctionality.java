package alpvax.advancedautocrafting.craftnetwork.function;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NodeFuctionality<T> implements ICapabilityProvider {
  public static final NodeFuctionality<IEnergyStorage> ENERGY = new NodeFuctionality<>() {
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> {
        //TODO: get from node
        return new EnergyStorage(10000);
      }));
    }
  };
}
