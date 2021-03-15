package jp.ac.titech.c.cl.chatannotator.util.handlers.event;

import java.util.Objects;

import io.netty.channel.Channel;
import jp.ac.titech.c.cl.chatannotator.common.config.ModConfig;
import jp.ac.titech.c.cl.chatannotator.network.ConfigMessage;
import jp.ac.titech.c.cl.chatannotator.util.Reference;
import jp.ac.titech.c.cl.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ConfigSyncEventHandler
{
	private static boolean isConfigLocked = false;

	/*
	 * SERVER SIDE: send config message whenever player logs in / kick if serverOnlyMode is off
	 */
	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onPlayerLoggedInEvent(PlayerLoggedInEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP)) return;

		EntityPlayerMP player = (EntityPlayerMP)event.player;

		if (isVanillaClient(player))
		{
			// Vanilla: kick if serverOnlyMode is off. No packet
			if (!ModConfig.serverOnlyMode)
			{
				String message = "Tried to connect with vanilla client.";
		        TextComponentString msgComponent = new TextComponentString(message);
				player.connection.disconnect(msgComponent);
			}
		}
		else
		{
			// Modded: sync config
			ChatAnnotatorPacketHandler.sendToClient(new ConfigMessage(ModConfig.isAnnotationEnabled()), player);
		}
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

    private static boolean isVanillaClient(EntityPlayerMP player)
    {
        NetHandlerPlayServer connection = player.connection;
        if (Objects.nonNull(connection))
        {
            NetworkManager netManager = connection.netManager;
            Channel channel = netManager.channel();
            return !channel.attr(NetworkRegistry.FML_MARKER).get();
        }

        return false;
    }
}
