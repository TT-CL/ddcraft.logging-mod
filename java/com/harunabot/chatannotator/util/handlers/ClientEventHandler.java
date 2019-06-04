package com.harunabot.chatannotator.util.handlers;

import com.harunabot.chatannotator.client.gui.GuiAnnotationButton;
import com.harunabot.chatannotator.client.gui.GuiChatWithAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event)
	{
		if(event.getGui() == null) return;

		// replace Chat GUI
		if (event.getGui() instanceof GuiChat) {
			event.setGui(new GuiChatWithAnnotation());
		}
	}
}
