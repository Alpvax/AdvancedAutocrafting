package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public interface INodePos extends Comparable<INodePos> {
    static boolean isEqual(@Nonnull INodePos a, @Nonnull INodePos b) {
        return a == b || a.getLevelKey().equals(b.getLevelKey()) && a.getBlockPos().equals(b.getBlockPos());
    }

    ResourceKey<Level> getLevelKey();

    default ChunkPos getChunkPos() {
        return new ChunkPos(getBlockPos());
    }

    BlockPos getBlockPos();

    @Override
    default int compareTo(@Nonnull INodePos o) {
        if (o == this) {
            return 0;
        }
        var cmp = getLevelKey().compareTo(o.getLevelKey());
        if (cmp == 0) {
            cmp = getBlockPos().compareTo(o.getBlockPos());
        }
        return cmp;
    }

    record Absolute(@Nonnull ResourceLocation levelID, @Nonnull BlockPos pos) implements INodePos {

        public static Absolute from(INodePos fromPos) {
            return new Absolute(fromPos.getLevelKey().location(), fromPos.getBlockPos());
        }

        @Override
        public ResourceKey<Level> getLevelKey() {
            return ResourceKey.create(Registry.DIMENSION_REGISTRY, levelID);
        }

        @Override
        public BlockPos getBlockPos() {
            return pos;
        }
    }
}
