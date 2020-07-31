package com.harunabot.chatannotator.screenshot.network;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.collection.generic.BitOperations.Int;

/*
 * A Message from Client side to tell the size of ScreenshotDataMessages.
 */
public class NotifyArrivalMessage implements IMessage
{
	private int imageId;
	private int parts;
	private int length;

	public NotifyArrivalMessage()
	{
	}

	public NotifyArrivalMessage(int imageId, int parts, int length)
	{
		this.imageId = imageId;
		this.parts = parts;
		this.length = length;
	}

	public int getImageId()
	{
		return imageId;
	}

	public int getPartsNum()
	{
		return parts;
	}

	public int getTotalLength()
	{
		return length;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		imageId = buf.readInt();
		parts = buf.readInt();
		length = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(imageId);
		buf.writeInt(parts);
		buf.writeInt(length);
	}

}