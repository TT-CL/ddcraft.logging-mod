package jp.ac.titech.c.cl.chatannotator.logger.network;

import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.logger.server.ChatRecorder;
import jp.ac.titech.c.cl.chatannotator.logger.server.json.ChatStatusJson;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
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

		ChatAnnotator.CHAT_RECORDER.recordPlayerVision(
				player,
				message.getSerialId(),
				message.getLookingAtPos(),
				message.getLookingAtName());

		return null;
	}

}
