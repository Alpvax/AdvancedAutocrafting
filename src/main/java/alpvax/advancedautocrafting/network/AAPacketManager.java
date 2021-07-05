package alpvax.advancedautocrafting.network;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class AAPacketManager {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(AdvancedAutocrafting.MODID, "main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
  );

  public static void registerPackets() {
    int id = 0;

    //HANDLER.registerMessage(id++, SyncBlockStatePacket.class, SyncBlockStatePacket::encode, SyncBlockStatePacket::decode, SyncBlockStatePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }

  /**
   * Sends a packet to the server.<br>
   * Must be called Client side.
   */
  public static <T> void sendToServer(T msg)
  {
    HANDLER.sendToServer(msg);
  }

  /**
   * Send a packet to a specific player.<br>
   * Must be called Server side.
   */
  public static <T> void sendTo(T msg, ServerPlayerEntity player)
  {
    if (!(player instanceof FakePlayer))
    {
      HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
  }
}
