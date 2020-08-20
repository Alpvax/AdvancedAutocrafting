package alpvax.advancedautocrafting.container.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContainerBlockHolder {
  private final BlockPos pos;
  private final ResourceLocation dimension;
  private BlockState state = Blocks.AIR.getDefaultState();
  private List<Consumer<ContainerBlockHolder>> listeners = new ArrayList<>();

  public ContainerBlockHolder(BlockPos pos, ResourceLocation dimension) {
    this.pos = pos;
    this.dimension = dimension;
  }

  public BlockPos getPos() {
    return pos;
  }
  public ResourceLocation getWorldID() {
    return dimension;
  }
  public BlockState getBlockState() {
    return state;
  }

  public ContainerBlockHolder setBlockState(@Nonnull BlockState state) {
    this.state = state;
    onChanged();
    return this;
  }

  private void onChanged() {
    listeners.forEach(l -> l.accept(this));
  }

  public void addListener(Consumer<ContainerBlockHolder> listener) {
    listeners.add(listener);
  }
  public void removeListener(Consumer<ContainerBlockHolder> listener) {
    listeners.remove(listener);
  }

  public boolean isClientWorld() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      Minecraft mc = Minecraft.getInstance();
      return mc.world != null && mc.world.func_234923_W_().func_240901_a_().equals(getWorldID());
    }
    return false;
  }

  public static ContainerBlockHolder from(PacketBuffer buf) {
    ContainerBlockHolder h = new ContainerBlockHolder(
        buf.readBlockPos(),
        buf.readResourceLocation()
    );
    h.setBlockState(Block.getStateById(buf.readVarInt()));
    return h;
  }

  public static void writeTo(ContainerBlockHolder instance, PacketBuffer buf) {
    buf.writeBlockPos(instance.getPos());
    buf.writeResourceLocation(instance.getWorldID());
    buf.writeVarInt(Block.getStateId(instance.state));
  }
}
