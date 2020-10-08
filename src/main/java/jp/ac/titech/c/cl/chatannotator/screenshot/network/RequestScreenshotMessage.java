package jp.ac.titech.c.cl.chatannotator.screenshot.network;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/*
 * A message from Server side to respond NotifyArrivalMessage & request ScreenshotDataMessages
 */
public class RequestScreenshotMessage implements IMessage
{
	String serialId;

	public RequestScreenshotMessage()
	{
	}

	public RequestScreenshotMessage(String serialId)
	{
		this.serialId = serialId;
	}

	String getserialId()
	{
		return serialId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		serialId = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, serialId);
	}
}
