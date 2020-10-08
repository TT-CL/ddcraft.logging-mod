package jp.ac.titech.c.cl.chatannotator.screenshot.network;

import org.apache.logging.log4j.Level;

import com.mojang.realmsclient.client.Request;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import jp.ac.titech.c.cl.chatannotator.screenshot.client.StandbyScreenshots;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRequestScreenshotMessage implements IMessageHandler<RequestScreenshotMessage, IMessage>
{
	@Override
	public IMessage onMessage(RequestScreenshotMessage message, MessageContext ctx)
	{
		ScreenRecorder.SCREENSHOT_HOLDER.sendSubScreenShotMessages(message.serialId);

		return null;
	}

}