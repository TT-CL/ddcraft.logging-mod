package com.harunabot.chatannotator.annotator.handler;

import com.harunabot.chatannotator.annotator.network.PlayerStateMessage;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
import com.ibm.icu.lang.UCharacter.SentenceBreak;

import akka.io.Udp.Send;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.collection.TraversableOnce.OnceCanBuildFrom;

@EventBusSubscriber
public class ChatEventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientChat(ClientChatEvent event)
	{
		String msg = event.getMessage();
		if (msg.startsWith("/"))
		{
			// ignore command
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		ChatAnnotatorPacketHandler.sendToServer(new PlayerStateMessage(mc.player, mc.world, mc.getRenderPartialTicks()));

	}
}
