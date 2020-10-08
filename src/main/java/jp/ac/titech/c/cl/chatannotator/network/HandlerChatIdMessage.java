package jp.ac.titech.c.cl.chatannotator.network;

import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.server.ChatIdManagerServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerChatIdMessage implements IMessageHandler<ChatIdMessage, IMessage>
{
	@Override
	public IMessage onMessage(ChatIdMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		player.getServer().addScheduledTask(new Runnable() {
			@Override
			public void run()
			{
				ChatAnnotator.CHAT_ID_MANAGER_SERVER.processChatIdMessage(message.getSerialId(), message.getMessage(), player);
			}
		});

		return null;
	}
}