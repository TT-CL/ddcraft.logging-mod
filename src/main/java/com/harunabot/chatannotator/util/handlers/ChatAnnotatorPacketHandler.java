package com.harunabot.chatannotator.util.handlers;

import java.util.Objects;

import com.harunabot.chatannotator.screenshot.network.HandlerRequestScreenshotMessage;
import com.harunabot.chatannotator.screenshot.network.HandlerScreenshotDataMessage;
import com.harunabot.chatannotator.screenshot.network.NotifyArrivalMessage;
import com.harunabot.chatannotator.annotator.network.ChatAnnotationMessage;
import com.harunabot.chatannotator.annotator.network.HandlerChatAnnotationMessage;
import com.harunabot.chatannotator.logger.network.HandlerPlayerStateMessage;
import com.harunabot.chatannotator.logger.network.PlayerStateMessage;
import com.harunabot.chatannotator.network.ChatIdMessage;
import com.harunabot.chatannotator.network.ConfigMessage;
import com.harunabot.chatannotator.network.HandlerChatIdMessage;
import com.harunabot.chatannotator.network.HandlerConfigMessage;
import com.harunabot.chatannotator.screenshot.network.HandlerNotifyArrivalMessage;
import com.harunabot.chatannotator.screenshot.network.RequestScreenshotMessage;
import com.harunabot.chatannotator.screenshot.network.ScreenshotDataMessage;
import com.harunabot.chatannotator.screenshot.network.ScreenshotDataMessage;
import com.harunabot.chatannotator.util.Reference;

import net.minecraft.entity.player.EntityPlayerMP;
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

		// Screenshot packets
		INSTANCE.registerMessage(HandlerNotifyArrivalMessage.class, NotifyArrivalMessage.class, discriminator++, Side.SERVER);
		INSTANCE.registerMessage(HandlerRequestScreenshotMessage.class, RequestScreenshotMessage.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(HandlerScreenshotDataMessage.class, ScreenshotDataMessage.class, discriminator++, Side.SERVER);

		// Chat annotation packets
		INSTANCE.registerMessage(HandlerPlayerStateMessage.class, PlayerStateMessage.class, discriminator++, Side.SERVER);
		INSTANCE.registerMessage(HandlerChatAnnotationMessage.class, ChatAnnotationMessage.class, discriminator++, Side.SERVER);

		// Config packets
		INSTANCE.registerMessage(HandlerConfigMessage.class, ConfigMessage.class, discriminator++, Side.CLIENT);

		// Chat common packet
		INSTANCE.registerMessage(HandlerChatIdMessage.class, ChatIdMessage.class, discriminator++, Side.SERVER);
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

	public static void sendToClient(IMessage message, EntityPlayerMP player)
	{
		if (Objects.isNull(INSTANCE))
		{
			System.err.println("Invalid access to network: mod not initialized");
			return;
		}

		INSTANCE.sendTo(message, player);
	}
}
