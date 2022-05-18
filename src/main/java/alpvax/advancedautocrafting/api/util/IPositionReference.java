package alpvax.advancedautocrafting.api.util;

import alpvax.advancedautocrafting.api.AAReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
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

/**
 * Represents a reference to a specific position in a specific dimension.<br>
 * <p>
 * Preferred structure for saving to NBT is to save the {@linkplain
 * #getPosition() position} to NBT using {@link NbtUtils#writeBlockPos} and
 * saving the {@link ResourceLocation#getPath path} of the {@linkplain
 * #getDimensionKey() dimension key} to the same compound with the key
 * "dimension". This results in a single tag with properties: {@literal "X"},
 * {@literal "Y"}, {@literal "Z"} and {@literal "dimension"}
 */
public interface IPositionReference {
    ResourceKey<Level> getDimensionKey();

    BlockPos getPosition();

    /**
     * Only call on logical server
     */
    default ServerLevel getLevel() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(getDimensionKey());
    }

    default boolean matchesLevel(@Nullable Level level) {
        return level != null && getDimensionKey().equals(level.dimension());
    }

    /**
     * Only call on logical server
     */
    default BlockSource toBlockSource() {
        return new BlockSourceImpl(getLevel(), getPosition());
    }

    static CompoundTag save(IPositionReference ref) {
        CompoundTag nbt = NbtUtils.writeBlockPos(ref.getPosition());
        nbt.putString("dimension", ref.getDimensionKey().location().toString());
        return nbt;
    }
    static IPositionReference load(CompoundTag nbt) {
        return new Impl(
            new ResourceLocation(nbt.getString("dimension")),
            NbtUtils.readBlockPos(nbt)
        );
    }

    record Impl(ResourceLocation dimension, BlockPos position) implements IPositionReference {
        public Impl {
            position = position.immutable();
        }

        public Impl(ResourceKey<Level> level, BlockPos pos) {
            this(level.location(), pos);
        }

        public Impl(Level level, BlockPos pos) {
            this(level.dimension().location(), pos);
        }

        public Impl(BlockSource source) {
            this(source.getLevel().dimension().location(), source.getPos());
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
        private static final String NBT_KEY = AAReference.MODID + ":position";
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
            return cap == AAReference.POSITION_MARKER_CAPABILITY && getTag().isPresent()
                   ? holder.cast()
                   : LazyOptional.empty();
        }

        @Override
        public ResourceKey<Level> getDimensionKey() {
            return ResourceKey.create(
                Registry.DIMENSION_REGISTRY, new ResourceLocation(getTag().orElseThrow().getString(DIM_KEY)));
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
