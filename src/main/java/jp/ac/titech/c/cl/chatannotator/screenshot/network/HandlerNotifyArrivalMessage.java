package jp.ac.titech.c.cl.chatannotator.screenshot.network;

import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerNotifyArrivalMessage implements IMessageHandler<NotifyArrivalMessage, RequestScreenshotMessage>
{
	@Override
	public RequestScreenshotMessage onMessage(NotifyArrivalMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		String serialId = message.getSerialId();

		if (serialId.isEmpty())
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Invalid screenshot message: something wrong on client.");
			return null;
		}

		ScreenRecorder.SCREENSHOT_LOG.reserveScreenshot(player, serialId, message.getPartsNum(), message.getTotalLength());

		return new RequestScreenshotMessage(serialId);
	}

}
