package com.harunabot.chatannotator;

import com.harunabot.chatannotator.util.Reference;
import com.harunabot.chatannotator.util.handlers.ChatHandler;
import com.harunabot.chatannotator.util.handlers.RegistryHandler;
import com.harunabot.chatannotator.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import scala.reflect.api.Internals.ReificationSupportApi.SyntacticClassDefExtractor;

@Mod(modid = Reference.MOD_ID, name= Reference.NAME, version = Reference.VERSION)
public class Main
{
	@Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
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
