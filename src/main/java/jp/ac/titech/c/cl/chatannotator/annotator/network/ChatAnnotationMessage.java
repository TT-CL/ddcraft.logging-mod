package jp.ac.titech.c.cl.chatannotator.annotator.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/*
 * A Message from Client side to tell the receiver's annotation for one chat.
 */
public class ChatAnnotationMessage implements IMessage
{
	private String senderId;
	private int chatId;
	private DialogueAct annotation;

	public ChatAnnotationMessage()
	{
	}

	public ChatAnnotationMessage(String senderId, int chatId, DialogueAct annotation)
	{
		this.senderId = senderId;
		this.chatId = chatId;
		this.annotation = annotation;
	}

	public String getSenderId()
	{
		return senderId;
	}

	public int getChatId()
	{
		return chatId;
	}

	public DialogueAct getAnnotation()
	{
		return annotation;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		senderId = ByteBufUtils.readUTF8String(buf);
		chatId = buf.readInt();
		int labelId = buf.readInt();
		annotation = DialogueAct.convertFromId(labelId);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, senderId);
		buf.writeInt(chatId);
		buf.writeInt(annotation.getId());
	}
}


