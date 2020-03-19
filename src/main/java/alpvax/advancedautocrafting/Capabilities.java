package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.block.tile.DummyNetworkNode;
import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.manager.NodeManager;
import alpvax.advancedautocrafting.item.IMultitool;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class Capabilities {
  @CapabilityInject(INetworkNode.class)
  public static Capability<INetworkNode> NODE_CAPABILITY = null;

  @CapabilityInject(IMultitool.class)
  public static Capability<IMultitool> MULTITOOL_CAPABILITY = null;

  @CapabilityInject(NodeManager.class)
  public static Capability<NodeManager> NODE_MANAGER_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(INetworkNode.class, new Capability.IStorage<INetworkNode>()
    {
      @Override
      public INBT writeNBT(Capability<INetworkNode> capability, INetworkNode instance, Direction side)
      {
        return null;
      }

      @Override
      public void readNBT(Capability<INetworkNode> capability, INetworkNode instance, Direction side, INBT base)
      {

      }
    }, () -> new DummyNetworkNode(ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD), BlockPos.ZERO));
    CapabilityManager.INSTANCE.register(IMultitool.class, new Capability.IStorage<IMultitool>()
    {
      @Override
      public INBT writeNBT(Capability<IMultitool> capability, IMultitool instance, Direction side)
      {
        return null;
      }

      @Override
      public void readNBT(Capability<IMultitool> capability, IMultitool instance, Direction side, INBT base)
      {

      }
    }, () -> new IMultitool() {});
    CapabilityManager.INSTANCE.register(NodeManager.class, new Capability.IStorage<NodeManager>() {
      @Nullable
      @Override
      public INBT writeNBT(Capability<NodeManager> capability, NodeManager instance, Direction side) {
        return null;
      }

      @Override
      public void readNBT(Capability<NodeManager> capability, NodeManager instance, Direction side, INBT nbt) {

      }
    }, () -> null);
  }

  public static void registerAttachEvents() {
    MinecraftForge.EVENT_BUS.addGenericListener(Chunk.class, EventPriority.LOWEST, Capabilities::attachChunk);
  }

  private static void attachChunk(AttachCapabilitiesEvent<Chunk> event) {
    if (event.getCapabilities().values().stream().noneMatch(p -> p.getCapability(NODE_MANAGER_CAPABILITY).isPresent())) {
      event.addCapability(
          new ResourceLocation(AdvancedAutocrafting.MODID, "node_manager_capability"),
          new NodeManager.Provider(event.getObject())
      );
    }
  }
}
