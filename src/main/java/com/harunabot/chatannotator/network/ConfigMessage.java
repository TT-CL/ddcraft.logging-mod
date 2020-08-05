package com.harunabot.chatannotator.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.xml.dtd.PublicID;

/* Message to tell the clients server's message*/
public class ConfigMessage implements IMessage
{
	private boolean isAnnotationEnabled;

	public ConfigMessage()
	{
	};

	public ConfigMessage(boolean isAnnotationEnabled)
	{
		this.isAnnotationEnabled = isAnnotationEnabled;
	}

	public boolean isAnnotationEnabled()
	{
		return isAnnotationEnabled;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		isAnnotationEnabled = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(isAnnotationEnabled);
	}

}
