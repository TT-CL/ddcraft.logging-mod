package com.harunabot.chatannotator.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChatHandler
{
	public static void preInit(FMLPreInitializationEvent event)
	{
		// make output file
		// initialize logger
	}

	public static void init(FMLInitializationEvent event)
	{
	}

	public static void postInit(FMLPostInitializationEvent event)
	{
	}


	@SubscribeEvent
	public static void onReceivedChat(ServerChatEvent event)
	{
		Style style = new Style();
		style.setColor(TextFormatting.AQUA);
		event.setComponent(event.getComponent().setStyle(style));

		System.out.println(event.getMessage());
	}
}
