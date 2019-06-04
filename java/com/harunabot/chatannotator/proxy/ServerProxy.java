package com.harunabot.chatannotator.proxy;


import com.harunabot.chatannotator.util.handlers.ChatHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Load resources and do some stuffs on the server side
 */
@Mod.EventBusSubscriber(Side.SERVER)
public class ServerProxy extends CommonProxy
{
	@Override
    public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		System.out.println("ServerProxy.preInit");
		ChatHandler.preInit(event);
    }

	@Override
    public void init(FMLInitializationEvent event) {
		super.init(event);

        System.out.println("ServerProxy.init");
        ChatHandler.init(event);
    }

	@Override
    public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

    	System.out.println("ServerProxy.postInit");
    	ChatHandler.postInit(event);
    }
}