package com.harunabot.chatannotator.client.gui.chat;

import java.io.IOException;

import javax.annotation.Resource;

import com.harunabot.chatannotator.util.Reference;
import com.mojang.realmsclient.dto.PlayerInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.model.b3d.B3DModel.Texture;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.reflect.internal.Trees.This;
import scala.reflect.internal.Types.ThisType;

public class GuiAnnotationButton extends GuiScreen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "texture/spacecat.jpg");
	private static int guiWidth = 355;
	private static int guiHeight= 321;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		drawTexturedModalRect(0, 0, 0, 0, guiWidth, guiHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui()
	{
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
	}
}
