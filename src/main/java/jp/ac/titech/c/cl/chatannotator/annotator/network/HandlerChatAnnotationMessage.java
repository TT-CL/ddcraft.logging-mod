package jp.ac.titech.c.cl.chatannotator.annotator.network;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import jp.ac.titech.c.cl.chatannotator.util.text.StringTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/*
 * Handles chat receiver's annotation on the serverside
 */
public class HandlerChatAnnotationMessage implements IMessageHandler<ChatAnnotationMessage, IMessage>
{
	@Override
	public IMessage onMessage(ChatAnnotationMessage msg, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		player.getServer().addScheduledTask(new Runnable() {
			@Override
			public void run()
			{
				onAnnotatedChat(msg.getSenderId(), msg.getChatId(), msg.getAnnotation(), player);
			}
		});

		return null;
	}

	protected static void onAnnotatedChat(String senderId, int chatId, DialogueAct annotation, EntityPlayerMP player)
	{
		int dimension = player.dimension;
		ChatAnnotator.ANNOTATION_RECORDER.annotateChat(annotation, chatId, senderId, player);
	}
}
