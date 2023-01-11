package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.core.BlockSource;

import java.util.Set;

public abstract class NodeConnection {
    private final boolean invalidated = false;
    private final boolean enabled = true;

    abstract Set<BlockSource> getContainedNodes();
}
