package jp.ac.titech.c.cl.chatannotator.network;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ChatIdMessage implements IMessage
{
	public static final int MAX_MESSAGE_LENGTH = 20;

	String serialId = "hoge";
	String message;

	public ChatIdMessage()
	{
	};

	public ChatIdMessage(String serialId, String message)
	{
		this.serialId = serialId;
		if (message.length() > MAX_MESSAGE_LENGTH){
			message = message.substring(0, MAX_MESSAGE_LENGTH);
		}
		this.message = message;
	}

	public String getSerialId()
	{
		return serialId;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		try
		{
			serialId = ByteBufUtils.readUTF8String(buf);
			message = ByteBufUtils.readUTF8String(buf);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			serialId = "hoge";
			message = "huga";
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, serialId);
		ByteBufUtils.writeUTF8String(buf, message);
	}

}
