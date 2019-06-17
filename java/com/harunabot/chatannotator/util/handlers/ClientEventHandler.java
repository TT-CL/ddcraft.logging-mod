package com.harunabot.chatannotator.util.handlers;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.GuiChatWithAnnotation;
import com.harunabot.chatannotator.client.gui.MyGuiNewChat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event)
	{
		if(event.getGui() == null) return;

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
	public static void replaceGuiNewChat()
	{
		GuiIngame ingameGUI = Minecraft.getMinecraft().ingameGUI;

		// Already replaced
		if(ingameGUI.getChatGUI() instanceof MyGuiNewChat) return;

		try
		{
			// Set new GuiNewChat
			MyGuiNewChat newGuiNewChat = new MyGuiNewChat(Minecraft.getMinecraft());
			Field chatGuiField = GuiIngame.class.getDeclaredField("persistantChatGUI");
			chatGuiField.setAccessible(true);
			chatGuiField.set(ingameGUI, newGuiNewChat);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			System.err.println("Failed to replace GuiNewChat!");
			e.printStackTrace();
		}

		// log
		ChatAnnotator.LOGGER.log(Level.INFO, "Replaced GuiNewChat: " + ingameGUI.getChatGUI().toString());
	}
}
