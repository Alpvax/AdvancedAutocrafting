package alpvax.advancedautocrafting.block.wire;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.api.wire.ISimpleWirePart;
import alpvax.advancedautocrafting.api.wire.IWirePart;
import alpvax.advancedautocrafting.api.wire.PartHitResult;
import alpvax.advancedautocrafting.block.wire.parts.None;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WirePartInstance<T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> implements INBTSerializable<CompoundTag> {
    private T part;
    private D data;
    WirePartInstance(T part, D data) {
        this.part = part;
        this.data = data;
    }
    WirePartInstance(T part) {
        this(part, part.initData());
    }
    static <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> WirePartInstance<T, D, N> create(T part) {
        return new WirePartInstance<>(part);
    }
    public static WirePartInstance<None, None.Connection, StringTag> none() {
        return new WirePartInstance<>((None) IWirePart.NONE.get());
    }
    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        nbt.putString(
            "part",
            RegistryManager.ACTIVE.getRegistry(AAReference.WIRE_PARTS).getResourceKey(part)
                .map(ResourceKey::location)
                .orElse(IWirePart.NONE.getId()).toString()
        );
        if (!(part instanceof ISimpleWirePart<?>)) {
            nbt.put("data", data.serializeNBT());
        }
        return nbt;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var partId = new ResourceLocation(nbt.getString("part"));
        part = (T) RegistryManager.ACTIVE.getRegistry(AAReference.WIRE_PARTS).getValue(partId);
        data.deserializeNBT((N) nbt.get("data"));
    }

    @SuppressWarnings("unchecked")
    static <T extends IWirePart<T, D>, D extends INBTSerializable<N>, N extends Tag> WirePartInstance<T, D, N> from(CompoundTag nbt) {
        var inst = new WirePartInstance<T, D, N>(
            (T) Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(AAReference.WIRE_PARTS)
                                           .getValue(new ResourceLocation(nbt.getString("part"))))
        );
        inst.deserializeNBT(nbt);
        return inst;
    }

    T getPart() {
        return part;
    }
    D getData() {
        return data;
    }
    void setData(D data) {
        this.data = data;
    }
    public String getModelKey() {
        return part.getModelKey(data);
    }

    boolean not(IWirePart<?,?> part) {
        return this.part != part;
    }
    boolean isNone() {
        return IWirePart.NONE.getId().equals(RegistryManager.ACTIVE.getRegistry(AAReference.WIRE_PARTS).getKey(part));
    }

    boolean canConnect() {
        return part.canConnect(data);
    }

    void onAdded(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        part.onAdded(data, level, position, direction, blockEntity);
    }
    void onRemoved(Level level, BlockPos position, Direction direction, WireBlockEntity blockEntity) {
        part.onRemoved(data, level, position, direction, blockEntity);
    }

    VoxelShape getShape(@NotNull Direction direction) {
        return part.getShape(data, direction);
    }
    public PartHitResult<T, D> rayTracePart(Direction direction, Vec3 start, Vec3 end, BlockPos pos) {
        return part.rayTracePart(data, direction, start, end, pos);
    }
}
