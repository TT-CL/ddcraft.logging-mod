package com.harunabot.chatannotator.annotator.network.handler;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.annotator.network.message.PlayerStateMessage;
import com.harunabot.chatannotator.annotator.server.ChatRecorder;
import com.harunabot.chatannotator.annotator.server.ChatStatusJson;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerPlayerStateMessage implements IMessageHandler<PlayerStateMessage, IMessage>
{
	@Override
	public IMessage onMessage(PlayerStateMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;

		ChatAnnotator.CHAT_RECORDER.recordChatStatus(
				player,
				message.getPlayerPos(),
				message.getPlayerLook(),
				message.getLookingAtPos(),
				message.getLookingAtName());

		return null;
	}

}
