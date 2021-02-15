package jp.ac.titech.c.cl.chatannotator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import jp.ac.titech.c.cl.chatannotator.annotator.server.AnnotationRecorder;
import jp.ac.titech.c.cl.chatannotator.client.ChatIdManagerClient;
import jp.ac.titech.c.cl.chatannotator.logger.server.ChatRecorder;
import jp.ac.titech.c.cl.chatannotator.proxy.CommonProxy;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import jp.ac.titech.c.cl.chatannotator.server.AlterDedicatedPlayerList;
import jp.ac.titech.c.cl.chatannotator.server.ChatIdManagerServer;
import jp.ac.titech.c.cl.chatannotator.util.Reference;
import jp.ac.titech.c.cl.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
		modid = Reference.MOD_ID,
		name= Reference.NAME,
		version = Reference.VERSION,
		acceptableRemoteVersions = "*",
		acceptedMinecraftVersions = "[1.12,1.13)"
	)
public class ChatAnnotator
{
	@Instance
	public static ChatAnnotator instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static Logger LOGGER;

	//public static ScreenRecorder SCREEN_RECORDER;
	@SideOnly(Side.SERVER)
	public static ChatRecorder CHAT_RECORDER;

	@SideOnly(Side.SERVER)
	public static AnnotationRecorder ANNOTATION_RECORDER;

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

	@SideOnly(Side.SERVER)
	@Mod.EventHandler
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		MinecraftServer server = event.getServer();
		if (!(server instanceof DedicatedServer)) return;

		DedicatedServer dedicatedServer = (DedicatedServer) server;
		server.setPlayerList(new AlterDedicatedPlayerList(dedicatedServer));

		ChatAnnotator.LOGGER.log(Level.INFO, "Replaced Server PlayerList: " + server.getPlayerList().toString());
	}
}
