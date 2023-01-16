package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.client.model.WireBakedModel;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import alpvax.advancedautocrafting.init.AABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class WireBlockEntity extends BlockEntity {
    private final INetworkNode node = makeNetworkNode();
    private final LazyOptional<INetworkNode> internalCapability = LazyOptional.of(() -> node);
    private final EnumMap<Direction, LazyOptional<INetworkNode>> sidedCapabilities = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, IWirePart> wireParts = new EnumMap<>(Direction.class);
//    private final EnumMap<Direction, Boolean> dirtyConnections = new EnumMap<>(Direction.class);

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(AABlocks.Entities.WIRE.get(), pos, state);
        for (var dir : Direction.values()) {
            sidedCapabilities.put(dir, LazyOptional.of(() -> node));
            wireParts.put(dir, IWirePart.BasicWireParts.NONE);
//            dirtyConnections.put(dir, true);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == AAReference.NODE_CAPABILITY) {
            if (side == null) {
                return internalCapability.cast();
            } else if (wireParts.get(side).canConnect()) {
                return sidedCapabilities.get(side).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private INetworkNode makeNetworkNode() {
        return new SimpleNetworkNode(worldPosition);//TODO
    }

    protected void setChanged(Direction direction) {
//        dirtyConnections.put(direction, true);
        super.setChanged();
        //noinspection ConstantConditions
        level.markAndNotifyBlock(
            worldPosition, level.getChunkAt(worldPosition),
            getBlockState(), getBlockState(),
            1 | 2 | 8, 512
        );
        var packet = getUpdatePacket();
        if (level != null && packet != null && !level.isClientSide) {
            ((ServerLevel) level).getChunkSource().chunkMap
                .getPlayers(new ChunkPos(worldPosition), false)
                .forEach(player -> player.connection.send(packet));
        }
    }

    private IWirePart calculateConnection(Direction direction, @Nullable BlockEntity neighbourBlockEntity, boolean ignoreDisabled) {
        var part = wireParts.get(direction);
        if (ignoreDisabled || part.canConnect()) {
            if (neighbourBlockEntity != null) {
                var cap = neighbourBlockEntity
                              .getCapability(AAReference.NODE_CAPABILITY, direction.getOpposite());
                //TODO: connect to adjacent node: cap.map(n -> n.connectTo(this))
                if (cap.isPresent()) {
                    return IWirePart.BasicWireParts.ARM;
                } else if (!neighbourBlockEntity.getCapability(AAReference.NODE_CAPABILITY, null).isPresent()) {
                //} else if (/*TODO: should connect capabilities?*/) {
                    return IWirePart.BasicWireParts.BLOCK_INTERFACE;
                }
            }
            return IWirePart.BasicWireParts.NONE;
        } else {
            return part;
        }
    }

    public boolean updateConnection(Direction direction, @Nullable BlockEntity neighbourBlockEntity) {
        return setPart(direction, calculateConnection(direction, neighbourBlockEntity, false));
    }

    public IWirePart getPart(Direction direction) {
        return wireParts.get(direction);
    }

    public boolean setPart(Direction direction, IWirePart part) {
        var prev = wireParts.get(direction);
        if (prev != part) {
            prev.onRemoved(level, worldPosition, direction, this);
            wireParts.put(direction, part);
            part.onAdded(level, worldPosition, direction, this);
            sidedCapabilities.get(direction).invalidate();
            sidedCapabilities.put(direction, LazyOptional.of(() -> node));
            setChanged(direction);
            return true;
        }
        return false;
    }
    public boolean removePart(Direction direction) {
        return level != null && setPart(direction, calculateConnection(direction, level.getBlockEntity(worldPosition), true));
    }

    boolean toggleDisabled(Direction direction) {
        var part = getPart(direction);
        if (part instanceof IWirePart.BasicWireParts) {
            if (part == IWirePart.BasicWireParts.DISABLED) {
                removePart(direction);
            } else {
                setPart(direction, IWirePart.BasicWireParts.DISABLED);
            }
        }
        return false;
    }

    @Override
    public @NotNull ModelData getModelData() {
        var b = ModelData.builder();
        wireParts.forEach((d, p) -> b.with(WireBakedModel.DIRECTION_DATA.get(d), p.getName()));
        return b.build();
    }

    @Override
    public CompoundTag getUpdateTag() {
        var nbt = super.getUpdateTag();
        var tag = new CompoundTag();
        wireParts.forEach((d, p) -> tag.putString(d.getName(), p.getName()));
        nbt.put("parts", tag);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        requestModelDataUpdate();
        //noinspection ConstantConditions
        level.markAndNotifyBlock(
            worldPosition, level.getChunkAt(worldPosition),
            getBlockState(), getBlockState(),
            1 | 2 | 8, 512
        );
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        CompoundTag tag = nbt.getCompound("parts");
        wireParts.entrySet().forEach(e -> {
            String dir = e.getKey().getName();
            if (tag.contains(dir, Tag.TAG_STRING)) {
                //TODO: load part from string
                e.setValue(IWirePart.BasicWireParts.valueOf(tag.getString(dir).toUpperCase()));
            }
        });
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        CompoundTag tag = new CompoundTag();
        wireParts.forEach((d, p) -> tag.putString(d.getName(), p.getName()));
        compound.put("parts", tag);
    }

    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//        handleUpdateTag(pkt.getTag());
//    }
}
