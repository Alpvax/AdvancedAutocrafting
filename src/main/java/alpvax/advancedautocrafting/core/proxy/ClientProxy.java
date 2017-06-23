package alpvax.advancedautocrafting.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void sendPlayerChatMessage(ITextComponent message, int replaceID)
	{
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(message, replaceID);
	}
}
