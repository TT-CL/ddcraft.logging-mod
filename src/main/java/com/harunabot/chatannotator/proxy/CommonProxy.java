package com.harunabot.chatannotator.proxy;

import com.harunabot.chatannotator.util.handlers.RegistryHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


/**
 * Load resources and do some stuffs on the both sides
 */
@Mod.EventBusSubscriber
public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
    	RegistryHandler.preInitRegistries(event);
    }

    public void init(FMLInitializationEvent event) {
    	RegistryHandler.initRegistries(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
    	RegistryHandler.postInitRegistries(event);
    }
}
