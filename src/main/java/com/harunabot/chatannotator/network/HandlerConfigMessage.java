package com.harunabot.chatannotator.network;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.common.config.AnnotationConfig;
import com.harunabot.chatannotator.util.handlers.event.ConfigSyncEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles ConfigMessage and overrides client config
 */
public class HandlerConfigMessage implements IMessageHandler<ConfigMessage, IMessage>
{
	@Override
	public IMessage onMessage(ConfigMessage message, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run()
			{
				AnnotationConfig.enableAnnotationLabel = message.isAnnotationEnabled();
				ConfigSyncEventHandler.lockConfig();

				String status = (AnnotationConfig.enableAnnotationLabel) ? "enabled" : "disabled";
				ChatAnnotator.LOGGER.log(Level.INFO, "Config overwritten by server: annotation " + status);
			}
		});

		return null;
	}
}
