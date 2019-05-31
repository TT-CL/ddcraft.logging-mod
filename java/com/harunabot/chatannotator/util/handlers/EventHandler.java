package com.harunabot.chatannotator.util.handlers;

import com.harunabot.chatannotator.Main;
import com.harunabot.chatannotator.client.gui.chat.GuiAnnotationButton;
import com.harunabot.chatannotator.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventHandler
{
	@SubscribeEvent
	public static void onGuiOoen(GuiOpenEvent event)
	{
		if(event.getGui() == null) return;

		if (event.getGui() instanceof GuiChat) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnnotationButton());
		}
	}

}
