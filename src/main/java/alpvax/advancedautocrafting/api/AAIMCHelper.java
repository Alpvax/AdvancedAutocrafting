package alpvax.advancedautocrafting.api;

import alpvax.advancedautocrafting.api.craftnetwork.NodeConnectivity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

public class AAIMCHelper {
    public enum IMCMethod implements StringRepresentable {
        REGISTER_CONNECTIVITY;

        private final String name;
        IMCMethod() {
            name = name().toLowerCase(Locale.ENGLISH);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    /**
     * Convenience method to send an IMC message to register a new blockstate connectivity factory.
     * Should be called from {@link net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent},
     * as it just wraps around {@link InterModComms#sendTo}
     * @param senderModId The modID of the mod sending this message.
     *                    If null then the sendTo method without the modId is called instead.
     * @param blockId the ResourceLocation of the block that can act as a wire
     * @param mapper a method which queries the connectivity of the given state.
     *               The state should always match the blockId
     * @return the value of the wrapped {@link InterModComms#sendTo} function
     * (whether or not the message was enqueued)
     */
    public boolean sendRegisterBlockStateConnectivity(@Nullable final String senderModId, final ResourceLocation blockId, final NodeConnectivity.IBlockStateConnectivityMapper mapper) {
        return sendIMC(senderModId, IMCMethod.REGISTER_CONNECTIVITY, () -> Pair.of(blockId, mapper));
    }

    @SuppressWarnings("SameParameterValue") //TODO: Remove warning suppression if/when more IMC types are added
    private <T> boolean sendIMC(@Nullable final String senderModId, IMCMethod method, Supplier<T> supplier) {
        return senderModId == null
            ? InterModComms.sendTo(AAReference.MODID, method.getSerializedName(), supplier)
            : InterModComms.sendTo(senderModId, AAReference.MODID, method.getSerializedName(), supplier);
    }
}
