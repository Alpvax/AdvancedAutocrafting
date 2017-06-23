package alpvax.advancedautocrafting.core.proxy;

import alpvax.advancedautocrafting.core.AdvancedAutocrafting;
import net.minecraft.util.text.ITextComponent;

public class CommonProxy
{
	public void sendPlayerChatMessage(ITextComponent message, int replaceID)
	{
		AdvancedAutocrafting.logger.warn("Cannot send replaceable chat message from serverside. ID: {}; Message: {}", replaceID, message);
	}
}
