package com.harunabot.chatannotator.screenshot.handler;

import java.io.File;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.xml.dtd.PublicID;

@Mod.EventBusSubscriber
public class TestEventHandler
{
	@SubscribeEvent
	public static void onSendClientChat(ClientChatEvent event)
	{
		ScreenRecorder.createScreenShot();
	}
}
