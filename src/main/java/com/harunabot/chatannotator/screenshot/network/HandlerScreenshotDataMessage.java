package com.harunabot.chatannotator.screenshot.network;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.screenshot.server.ScreenshotLog;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerScreenshotDataMessage implements IMessageHandler<ScreenshotDataMessage, IMessage>
{
	@Override
	public IMessage onMessage(ScreenshotDataMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		ScreenRecorder.SCREENSHOT_LOG.saveSubData(player, message.getSerialId(), message.getPartId(), message.getBytes());

		return null;
	}

}
