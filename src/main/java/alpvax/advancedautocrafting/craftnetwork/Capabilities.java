package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedAutocrafting.MODID)
public class Capabilities {
  @CapabilityInject(INetworkNode.class)
  public static Capability<INetworkNode> NODE_CAPABILITY = null;

  @CapabilityInject(INetworkManager.class)
  public static Capability<INetworkManager> MANAGER_CAPABILITY = null;

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
    }, () -> new SimpleNetworkNode(BlockPos.ZERO));
    CapabilityManager.INSTANCE.register(INetworkManager.class, new Capability.IStorage<INetworkManager>()
    {
      @Override
      public INBT writeNBT(Capability<INetworkManager> capability, INetworkManager instance, Direction side)
      {
        return instance.serializeNBT();
      }

      @Override
      public void readNBT(Capability<INetworkManager> capability, INetworkManager instance, Direction side, INBT base)
      {
        instance.deserializeNBT((CompoundNBT) base);
      }
    }, WorldNetworkManager::new);
  }

  public static void attachWorldCaps(AttachCapabilitiesEvent<World> event) {
    event.addCapability(new ResourceLocation(AdvancedAutocrafting.MODID, "network_manager_cap"), new WorldNetworkManager.Provider(event.getObject()));
  }
}
