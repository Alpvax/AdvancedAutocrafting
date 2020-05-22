package alpvax.advancedautocrafting;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import alpvax.advancedautocrafting.craftnetwork.SimpleNetworkNode;
import alpvax.advancedautocrafting.item.IMultitool;
import alpvax.advancedautocrafting.souls.ISoulTracker;
import alpvax.advancedautocrafting.souls.PlayerSoulSliver;
import alpvax.advancedautocrafting.souls.PlayerSoulTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Capabilities {
  @CapabilityInject(INetworkNode.class)
  public static Capability<INetworkNode> NODE_CAPABILITY = null;

  @CapabilityInject(IMultitool.class)
  public static Capability<IMultitool> MULTITOOL_CAPABILITY = null;

  @CapabilityInject(ISoulTracker.class)
  public static Capability<ISoulTracker> SOUL_TRACKER_CAPABILITY = null;
  @CapabilityInject(PlayerSoulSliver.class)
  public static Capability<PlayerSoulSliver> SOUL_SLIVER_CAPABILITY = null;

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
    CapabilityManager.INSTANCE.register(ISoulTracker.class, new Capability.IStorage<ISoulTracker>()
    {
      @Override
      public INBT writeNBT(Capability<ISoulTracker> capability, ISoulTracker instance, Direction side)
      {
        return null;
      }

      @Override
      public void readNBT(Capability<ISoulTracker> capability, ISoulTracker instance, Direction side, INBT base)
      {

      }
    }, () -> new ISoulTracker() {
      @Override
      public boolean canHarvest(UUID targetID) {
        return false;
      }
      @Nonnull
      @Override
      public ItemStack getSoulSliver(@Nullable UUID targetID) {
        return ItemStack.EMPTY;
      }

      @Override
      public void addSliver(UUID playerID) {}
    });
    CapabilityManager.INSTANCE.register(PlayerSoulSliver.class, new Capability.IStorage<PlayerSoulSliver>()
    {
      @Override
      public INBT writeNBT(Capability<PlayerSoulSliver> capability, PlayerSoulSliver instance, Direction side)
      {
        return null;
      }

      @Override
      public void readNBT(Capability<PlayerSoulSliver> capability, PlayerSoulSliver instance, Direction side, INBT base)
      {

      }
    }, () -> new PlayerSoulSliver() {});

    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, Capabilities::attachEntityCapabilities);
  }

  private static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
    Entity e = event.getObject();
    if (e instanceof PlayerEntity && !e.world.isRemote) {
      event.addCapability(
          new ResourceLocation(AdvancedAutocrafting.MODID, "soul_tracker"),
          new PlayerSoulTracker.Provider((ServerPlayerEntity)e)
      );
    }
  }
}
