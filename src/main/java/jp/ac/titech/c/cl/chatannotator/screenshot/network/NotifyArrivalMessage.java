package jp.ac.titech.c.cl.chatannotator.screenshot.network;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.collection.generic.BitOperations.Int;

/*
 * A Message from Client side to tell the size of ScreenshotDataMessages.
 */
public class NotifyArrivalMessage implements IMessage
{
	private String serialId;
	private int parts;
	private int length;

	public NotifyArrivalMessage()
	{
	}

	public NotifyArrivalMessage(String serialId, int parts, int length)
	{
		this.serialId = serialId;
		this.parts = parts;
		this.length = length;
	}

	public String getSerialId()
	{
		return serialId;
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
		serialId = ByteBufUtils.readUTF8String(buf);
		parts = buf.readInt();
		length = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, serialId);
		buf.writeInt(parts);
		buf.writeInt(length);
	}

}