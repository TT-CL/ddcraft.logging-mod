package com.harunabot.chatannotator.logger.network.message;

import java.nio.charset.Charset;

import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.typesafe.config.ConfigException.BugOrBroken;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/*
 * A Message from Client side to tell the player's annotation for one chat.
 */
public class ChatAnnotationMessage implements IMessage
{
	private String serialId;
	private DialogueAct annotation;
	private boolean isSender;

	public ChatAnnotationMessage()
	{
	}

	public ChatAnnotationMessage(String serialId, DialogueAct annotation, boolean isSender)
	{
		this.serialId = serialId;
		this.annotation = annotation;
		this.isSender = isSender;
	}

	public String getSerialId()
	{
		return serialId;
	}

	public DialogueAct getAnnotation()
	{
		return annotation;
	}

	public boolean isSenderMessage()
	{
		return isSender;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int strlen = buf.readInt();
		serialId = buf.readCharSequence(strlen, Charset.defaultCharset()).toString();

		int labelId = buf.readInt();
		annotation = DialogueAct.convertFromId(labelId);

		isSender = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(serialId.length());
		buf.writeCharSequence(serialId, Charset.defaultCharset());

		buf.writeInt(annotation.getId());

		buf.writeBoolean(isSender);
	}
}


