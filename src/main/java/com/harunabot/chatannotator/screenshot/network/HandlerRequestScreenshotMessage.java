package com.harunabot.chatannotator.screenshot.network;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.screenshot.client.StandbyScreenshots;
import com.mojang.realmsclient.client.Request;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRequestScreenshotMessage implements IMessageHandler<RequestScreenshotMessage, IMessage>
{
	@Override
	public IMessage onMessage(RequestScreenshotMessage message, MessageContext ctx)
	{
		ScreenRecorder.SCREENSHOT_HOLDER.sendSubScreenShotMessages(message.imageId);

		return null;
	}

}
