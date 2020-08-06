package com.harunabot.chatannotator.util.handlers.event;

import com.harunabot.chatannotator.common.config.AnnotationConfig;
import com.harunabot.chatannotator.network.ConfigMessage;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ConfigSyncEventHandler
{
	private static boolean isConfigLocked = false;

	/*
	 * SERVER SIDE: send config message whenever player log in
	 */
	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onPlayerLoggedInEvent(PlayerLoggedInEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP)) return;

		EntityPlayerMP player = (EntityPlayerMP)event.player;
		ChatAnnotatorPacketHandler.sendToClient(new ConfigMessage(AnnotationConfig.enableAnnotationLabel), player);
	}

	/*
	 * CLIENT SIDE: lock config change after its locked
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onConfigChangedEvent(ConfigChangedEvent event)
	{
		if (isConfigLocked && event.isCancelable())
		{
			event.setCanceled(true);
		}
	}

	public static void lockConfig()
	{
		isConfigLocked = true;
	}
}
