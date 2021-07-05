package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import alpvax.advancedautocrafting.item.IMultitool;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {
  @CapabilityInject(INetworkNode.class)
  public static Capability<INetworkNode> NODE_CAPABILITY = null;

  @CapabilityInject(IMultitool.class)
  public static Capability<IMultitool> MULTITOOL_CAPABILITY = null;

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
    }, () -> new SimpleNetworkNode(BlockPos.ZERO, DimensionType.OVERWORLD_LOCATION.location()));
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
  }
}
