package com.harunabot.chatannotator.screenshot.network;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/*
 * A message from Server side to respond NotifyArrivalMessage & request ScreenshotDataMessages
 */
public class RequestScreenshotMessage implements IMessage
{
	int imageId;

	public RequestScreenshotMessage()
	{
	}

	public RequestScreenshotMessage(int imageId)
	{
		this.imageId = imageId;
	}

	int getImageId()
	{
		return imageId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		imageId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(imageId);

	}

}
