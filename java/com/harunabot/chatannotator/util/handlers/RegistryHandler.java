package com.harunabot.chatannotator.util.handlers;

import com.harunabot.chatannotator.Main;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@EventBusSubscriber
public class RegistryHandler
{
	public static void preInitRegistries(FMLPreInitializationEvent event)
	{
		//BlockInit.registerBlocks();
	}

	public static void initRegistries(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}

	public static void postInitRegistries(FMLPostInitializationEvent event)
	{

	}

}
