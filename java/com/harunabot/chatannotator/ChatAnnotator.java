package com.harunabot.chatannotator;

import org.apache.logging.log4j.Logger;

import com.harunabot.chatannotator.proxy.CommonProxy;
import com.harunabot.chatannotator.util.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name= Reference.NAME, version = Reference.VERSION)
public class ChatAnnotator
{
	@Instance
	public static ChatAnnotator instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static Logger LOGGER;

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
	}

	@Mod.EventHandler
	public static void PostInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

}
