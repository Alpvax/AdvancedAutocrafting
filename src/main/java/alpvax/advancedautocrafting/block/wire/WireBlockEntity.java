package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.block.wire.parts.None;
import alpvax.advancedautocrafting.client.model.WireBakedModel;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import alpvax.advancedautocrafting.init.AABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class WireBlockEntity extends BlockEntity {
    private final INetworkNode node = makeNetworkNode();
    private final LazyOptional<INetworkNode> internalCapability = LazyOptional.of(() -> node);
    private final EnumMap<Direction, LazyOptional<INetworkNode>> sidedCapabilities = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, WirePartInstance<?, ?, ?>> wireParts = new EnumMap<>(Direction.class);
//    private final EnumMap<Direction, Boolean> dirtyConnections = new EnumMap<>(Direction.class);

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(AABlocks.Entities.WIRE.get(), pos, state);
        for (var dir : Direction.values()) {
            sidedCapabilities.put(dir, LazyOptional.of(() -> node));
            wireParts.put(dir, WirePartInstance.none());
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

//    private IWirePart<?, ?> calculateConnection(Direction direction, @Nullable BlockEntity neighbourBlockEntity, boolean ignoreDisabled) {
//        var part = wireParts.get(direction);
//        if (ignoreDisabled || part.canConnect()) {
//            if (neighbourBlockEntity != null) {
//                var cap = neighbourBlockEntity
//                              .getCapability(AAReference.NODE_CAPABILITY, direction.getOpposite());
//                //TODO: connect to adjacent node: cap.map(n -> n.connectTo(this))
//                if (cap.isPresent()) {
//                    return IWirePart.BasicWireParts.ARM;
//                } else if (!neighbourBlockEntity.getCapability(AAReference.NODE_CAPABILITY, null).isPresent()) {
//                //} else if (/*TODO: should connect capabilities?*/) {
//                    return IWirePart.BasicWireParts.BLOCK_INTERFACE;
//                }
//            }
//            return IWirePart.BasicWireParts.NONE;
//        } else {
//            return part;
//        }
//    }

    @SuppressWarnings("unchecked")
    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag>
    WirePartInstance<T, D, N> getPartInstance(Direction direction) {
        return (WirePartInstance<T, D, N>) wireParts.get(direction);
    }
    @SuppressWarnings("unchecked")
    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> T getPart(Direction direction) {
        return (T) wireParts.get(direction).getPart();
    }
    @SuppressWarnings("unchecked")
    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> D getData(Direction direction) {
        return (D) wireParts.get(direction).getData();
    }
    @SuppressWarnings("unchecked")
    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> void setData(Direction direction, D data) {
        ((WirePartInstance<T, D, N>) wireParts.get(direction)).setData(data);
        setChanged(direction);
    }

    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> VoxelShape getPartShape(Direction direction) {
        return wireParts.get(direction).getShape(direction);
    }

    public boolean updateConnection(Direction direction, @Nullable BlockEntity neighbourBlockEntity) {
        var partInst = wireParts.get(direction);
        None.Connection data;
        if (partInst.isNone() && (data = (None.Connection) partInst.getData()) != None.Connection.DISABLED) {
            var newData = ((None) partInst.getPart()).calculateConnection(data, direction, neighbourBlockEntity, false);
            if (newData != data) {
                setData(direction, newData);
                return true;
            }
        }
        return false;
    }

    public <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag>
    boolean setPart(Direction direction, T part) {
        var prev = wireParts.get(direction);
        if (prev.not(part)) {
            prev.onRemoved(level, worldPosition, direction, this);
            var newInst = new WirePartInstance<>(part);
            wireParts.put(direction, newInst);
            newInst.onAdded(level, worldPosition, direction, this);
            sidedCapabilities.get(direction).invalidate();
            sidedCapabilities.put(direction, LazyOptional.of(() -> node));
            setChanged(direction);
            return true;
        }
        return false;
    }
    public boolean removePart(Direction direction) {
        return level != null && setPart(
            direction, (None) IWirePart.NONE.get());
    }

    void toggleDisabled(Direction direction) {
        var partInst = wireParts.get(direction);
        if (partInst.isNone()) {
            var data = (None.Connection) partInst.getData();
            @SuppressWarnings("ConstantConditions")
            var newData = data == None.Connection.DISABLED
                ? ((None) partInst.getPart()).calculateConnection(data, direction, level.getBlockEntity(worldPosition.relative(direction)), false)
                : None.Connection.DISABLED;
            if (newData != data) {
                setData(direction, newData);
            }
        }
    }

    @Override
    public @NotNull ModelData getModelData() {
        var b = ModelData.builder();
        wireParts.forEach((d, p) -> b.with(WireBakedModel.DIRECTION_DATA.get(d), p.getModelKey()));
        return b.build();
    }

    @Override
    public CompoundTag getUpdateTag() {
        var nbt = super.getUpdateTag();
        var tag = new CompoundTag();
        wireParts.forEach((d, p) -> tag.put(d.getName(), p.serializeNBT()));
        nbt.put("parts", tag);
        return nbt;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getTag());
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
            if (tag.contains(dir, Tag.TAG_COMPOUND)) {
                e.setValue(WirePartInstance.from(tag.getCompound(dir)));
            }
        });
        //TODO: schedule tick
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        CompoundTag tag = new CompoundTag();
        wireParts.forEach((d, p) -> tag.put(d.getName(), p.serializeNBT()));
        compound.put("parts", tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//        handleUpdateTag(pkt.getTag());
//    }
}
