package com.harunabot.chatannotator.screenshot.network;

import com.google.common.primitives.Bytes;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.collection.generic.BitOperations.Int;


/**
 * Message from Client side to send sub array of the screenshot byte array
 */
public class ScreenshotDataMessage implements IMessage
{
	private int imageId;
	private int partId;
	private byte[] bytes;

	public ScreenshotDataMessage()
	{
	}

	public ScreenshotDataMessage(int imageId, int partId, byte[] bytes)
	{
		this.imageId = imageId;
		this.partId = partId;
		this.bytes = bytes;
	}

	int getImageId()
	{
		return imageId;
	}

	int getPartId()
	{
		return partId;
	}

	byte[] getBytes()
	{
		return bytes;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		imageId = buf.readInt();
		partId = buf.readInt();
		bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(imageId);
		buf.writeInt(partId);
		buf.writeBytes(bytes);
	}

}
