package com.harunabot.chatannotator.screenshot.network;

import com.google.common.primitives.Bytes;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.collection.generic.BitOperations.Int;


/**
 * Message from Client side to send sub array of the screenshot byte array
 */
public class ScreenshotDataMessage implements IMessage
{
	private String serialId;
	private int partId;
	private byte[] bytes;

	public ScreenshotDataMessage()
	{
	}

	public ScreenshotDataMessage(String serialId, int partId, byte[] bytes)
	{
		this.serialId = serialId;
		this.partId = partId;
		this.bytes = bytes;
	}

	String getSerialId()
	{
		return serialId;
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
		serialId = ByteBufUtils.readUTF8String(buf);
		partId = buf.readInt();
		bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, serialId);
		buf.writeInt(partId);
		buf.writeBytes(bytes);
	}

}
