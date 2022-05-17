package alpvax.advancedautocrafting.network;

import alpvax.advancedautocrafting.api.AAReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AAPacketManager {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(AAReference.MODID, "main"),
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
    public static <T> void sendToServer(T msg) {
        HANDLER.sendToServer(msg);
    }

    /**
     * Send a packet to a specific player.<br>
     * Must be called Server side.
     */
    public static <T> void sendTo(T msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
