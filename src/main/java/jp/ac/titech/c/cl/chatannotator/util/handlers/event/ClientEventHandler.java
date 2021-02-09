package jp.ac.titech.c.cl.chatannotator.util.handlers.event;

import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.client.gui.AlterGuiNewChat;
import jp.ac.titech.c.cl.chatannotator.annotator.client.gui.GuiChatWithAnnotation;
import jp.ac.titech.c.cl.chatannotator.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler
{
	// Field index for reflection
	protected static final int PERSISTANTCHATGUI_FIELD_INDEX = 6;

	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event)
	{
		if (!ModConfig.enableAnnotationLabel) return;
		if (event.getGui() == null) return;

		// replace Chat GUI
		if (event.getGui() instanceof GuiChat) {
			event.setGui(new GuiChatWithAnnotation());
		}
	}

	@SubscribeEvent
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		replaceGuiNewChat();
	}


	/***
	 * Replace GUINewChat
	 */
	protected static void replaceGuiNewChat()
	{
		GuiIngame ingameGUI = Minecraft.getMinecraft().ingameGUI;
		GuiNewChat originalGuiNewChat = ingameGUI.getChatGUI();

		// Already replaced
		if(originalGuiNewChat instanceof AlterGuiNewChat) return;

		try
		{
			// Set new GuiNewChat
			AlterGuiNewChat newGuiNewChat = new AlterGuiNewChat(Minecraft.getMinecraft());
			ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, ingameGUI, newGuiNewChat, PERSISTANTCHATGUI_FIELD_INDEX);
		}
		catch(UnableToFindFieldException | UnableToAccessFieldException e)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Reflection Error: Failed to replace GuiNewChat!");
			throw e;
		}

		// log
		ChatAnnotator.LOGGER.log(Level.INFO, "Replaced GuiNewChat: " + ingameGUI.getChatGUI().toString());
	}
}
