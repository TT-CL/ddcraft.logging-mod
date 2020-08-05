package com.harunabot.chatannotator.proxy;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.ChatIdManagerClient;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Load resources and do some stuffs on the client side
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
    public void preInit(FMLPreInitializationEvent event) {
    	super.preInit(event);
	}

	@Override
    public void init(FMLInitializationEvent event) {
		super.init(event);

		ChatAnnotator.CHAT_ID_MANAGER_CLIENT = new ChatIdManagerClient();
    }

	@Override
    public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
    }

}