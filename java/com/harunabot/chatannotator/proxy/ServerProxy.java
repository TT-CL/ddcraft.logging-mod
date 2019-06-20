package com.harunabot.chatannotator.proxy;


import com.harunabot.chatannotator.util.handlers.ChatEventHandler;

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
		ChatEventHandler.preInit(event);
    }

	@Override
    public void init(FMLInitializationEvent event) {
		super.init(event);
    }

	@Override
    public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
    }
}