package jp.ac.titech.c.cl.chatannotator.util.handlers.event;

import jp.ac.titech.c.cl.chatannotator.common.config.AnnotationConfig;
import jp.ac.titech.c.cl.chatannotator.network.ConfigMessage;
import jp.ac.titech.c.cl.chatannotator.util.Reference;
import jp.ac.titech.c.cl.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
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
		if (isConfigLocked && event.isCancelable() && event.getModID().equals(Reference.MOD_ID))
		{
			event.setCanceled(true);
		}
	}

	public static void lockConfig()
	{
		isConfigLocked = true;
	}
}
