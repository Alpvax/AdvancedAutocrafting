package alpvax.advancedautocrafting.util;

import alpvax.advancedautocrafting.api.util.IPositionReference;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Objects;

public interface ICapabilityReference<T> {
    IPositionReference getPosition();

    Capability<T> getCapabilityType();

    LazyOptional<T> getCapability();

    @Nullable
    Direction getCapabliltySide();

    class Impl<T> implements ICapabilityReference<T> {
        private final Capability<T> capability;
        @Nullable
        private final Direction side;
        private final IPositionReference posRef;
        private LazyOptional<T> capInstance;

        public Impl(Capability<T> capability, @Nullable Direction side, IPositionReference position) {
            this.capability = capability;
            this.side = side;
            posRef = position;
            updateCapInstance();
        }

        @Override
        public IPositionReference getPosition() {
            return posRef;
        }

        @Override
        public Capability<T> getCapabilityType() {
            return capability;
        }

        @Override
        public LazyOptional<T> getCapability() {
            if (!capInstance.isPresent()) {
                updateCapInstance();
            }
            return capInstance;
        }

        @Nullable
        @Override
        public Direction getCapabliltySide() {
            return side;
        }

        protected void updateCapInstance() {
            capInstance = getPosition().getLevel().getCapability(getCapabilityType(), getCapabliltySide());
        }

        @Override
        public int hashCode() {
            return Objects.hash(capability, side, posRef);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ICapabilityReference other) {
                return capability == other.getCapabilityType()
                       && side == other.getCapabliltySide()
                       && posRef.equals(other.getPosition());
            }
            return false;
        }
    }
}
