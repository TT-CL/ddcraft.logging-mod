package com.harunabot.chatannotator.util.handlers;

import java.util.Objects;

import com.harunabot.chatannotator.screenshot.network.HandlerRequestScreenshotMessage;
import com.harunabot.chatannotator.screenshot.network.HandlerScreenshotDataMessage;
import com.harunabot.chatannotator.screenshot.network.NotifyArrivalMessage;
import com.harunabot.chatannotator.screenshot.network.HandlerNotifyArrivalMessage;
import com.harunabot.chatannotator.screenshot.network.RequestScreenshotMessage;
import com.harunabot.chatannotator.screenshot.network.ScreenshotDataMessage;
import com.harunabot.chatannotator.screenshot.network.ScreenshotDataMessage;
import com.harunabot.chatannotator.util.Reference;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ChatAnnotatorPacketHandler
{
	private static final String PROTOCOL_VERSION = "1";
	private static SimpleNetworkWrapper INSTANCE;

	public static void init(FMLInitializationEvent event)
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
		register();
	}

	public static void register()
	{
		int discriminator = 0;

		INSTANCE.registerMessage(HandlerNotifyArrivalMessage.class, NotifyArrivalMessage.class, discriminator++, Side.SERVER);
		INSTANCE.registerMessage(HandlerRequestScreenshotMessage.class, RequestScreenshotMessage.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(HandlerScreenshotDataMessage.class, ScreenshotDataMessage.class, discriminator++, Side.SERVER);
	}

	public static void sendToServer(IMessage message)
	{
		if (Objects.isNull(INSTANCE))
		{
			System.err.println("Invalid access to network: mod not initialized");
			return;
		}

		INSTANCE.sendToServer(message);
	}
}
