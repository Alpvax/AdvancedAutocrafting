package alpvax.advancedautocrafting.util;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface IPositionReference {
    ResourceKey<Level> getDimensionKey();

    /**
     * Only call on logical server
     */
    default ServerLevel getLevel() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(getDimensionKey());
    }

    BlockPos getPosition();

    default boolean matchesLevel(@Nullable Level level) {
        return level != null && getDimensionKey().equals(level.dimension());
    }

//    /**
//     * Only call on logical server
//     */
//    default BlockSource toBlockSource() {
//        return new BlockSourceImpl(getLevel(), getPosition());
//    }

    class Impl implements IPositionReference {
        private final ResourceLocation dimension;
        private final BlockPos position;

        public Impl(ResourceLocation level, BlockPos pos) {
            dimension = level;
            position = pos.immutable();
        }

        public Impl(ResourceKey<Level> level, BlockPos pos) {
            dimension = level.location();
            position = pos.immutable();
        }

        public Impl(Level level, BlockPos pos) {
            dimension = level.dimension().location();
            position = pos.immutable();
        }

        @Override
        public ResourceKey<Level> getDimensionKey() {
            return ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension);
        }

        @Override
        public BlockPos getPosition() {
            return position;
        }
    }

    class PositionMarkerItemStack implements IPositionReference, ICapabilityProvider {
        private static final String NBT_KEY = AdvancedAutocrafting.MODID + ":position";
        private static final String DIM_KEY = "dimension";

        private final LazyOptional<IPositionReference> holder = LazyOptional.of(() -> this);

        private final ItemStack stack;

        public PositionMarkerItemStack(ItemStack stack) {
            this.stack = stack;
        }

        private Optional<CompoundTag> getTag() {
            var tag = stack.getTag();
            if (tag != null && tag.contains(NBT_KEY, Tag.TAG_COMPOUND)) {
                return Optional.of(tag.getCompound(NBT_KEY));
            }
            return Optional.empty();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == Capabilities.POSITION_MARKER_CAPABILITY && getTag().isPresent() ? holder.cast() : LazyOptional.empty();
        }

        @Override
        public ResourceKey<Level> getDimensionKey() {
            return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(getTag().orElseThrow().getString(DIM_KEY)));
        }

        @Override
        public BlockPos getPosition() {
            return NbtUtils.readBlockPos(getTag().orElseThrow());
        }

        public static void setPosition(ItemStack stack, ResourceKey<Level> dimensionKey, BlockPos pos) {
            CompoundTag nbt = NbtUtils.writeBlockPos(pos);
            nbt.putString(DIM_KEY, dimensionKey.location().toString());
            stack.addTagElement(NBT_KEY, nbt);
        }
    }
}
