package com.harunabot.chatannotator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.harunabot.chatannotator.client.ChatIdManagerClient;
import com.harunabot.chatannotator.logger.server.ChatRecorder;
import com.harunabot.chatannotator.proxy.CommonProxy;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.server.ChatIdManagerServer;
import com.harunabot.chatannotator.util.Reference;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Reference.MOD_ID, name= Reference.NAME, version = Reference.VERSION)
public class ChatAnnotator
{
	@Instance
	public static ChatAnnotator instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static Map<Integer, AnnotationLog> annotationLogs = new HashMap<>();

	public static Logger LOGGER;

	//public static ScreenRecorder SCREEN_RECORDER;
	@SideOnly(Side.SERVER)
	public static ChatRecorder CHAT_RECORDER;

	@SideOnly(Side.SERVER)
	public static ChatIdManagerServer CHAT_ID_MANAGER_SERVER;

	@SideOnly(Side.CLIENT)
	public static ChatIdManagerClient CHAT_ID_MANAGER_CLIENT;


	// Directory for saving files
	public static final File modDirectory = new File(Reference.MOD_ID);
	public static Map<Integer, File> dimensionDirectories = new HashMap<>();

	@Mod.EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
		LOGGER = event.getModLog();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event)
	{
		proxy.init(event);

		// proxy?
		ChatAnnotatorPacketHandler.init(event);
		ScreenRecorder.init();
	}

	@Mod.EventHandler
	public static void PostInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

}
