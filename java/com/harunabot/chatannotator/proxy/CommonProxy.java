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
    	System.out.println("CommonProxy.preInit");

		RegistryHandler.preInitRegistries(event);
    }

    public void init(FMLInitializationEvent event) {
        System.out.println("CommonProxy.init");

        RegistryHandler.initRegistries(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
    	System.out.println("CommonProxy.postInit");

		RegistryHandler.postInitRegistries(event);
    }
}
