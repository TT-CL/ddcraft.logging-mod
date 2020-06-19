package com.harunabot.chatannotator.screenshot.network;

import java.beans.Customizer;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.mojang.realmsclient.client.Request;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerNotifyArrivalMessage implements IMessageHandler<NotifyArrivalMessage, RequestScreenshotMessage>
{
	@Override
	public RequestScreenshotMessage onMessage(NotifyArrivalMessage message, MessageContext ctx)
	{

		int imageId = message.getImageId();

		if (imageId == -1)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Invalid screenshot message: something wrong on client.");
			return null;
		}

		ScreenRecorder.SCREENSHOT_LOG.reserveScreenshot(imageId, message.getPartsNum(), message.getTotalLength());
		ChatAnnotator.LOGGER.log(Level.INFO, "Reserved Screenshot for id: " + imageId);

		return new RequestScreenshotMessage(imageId);
	}

}
