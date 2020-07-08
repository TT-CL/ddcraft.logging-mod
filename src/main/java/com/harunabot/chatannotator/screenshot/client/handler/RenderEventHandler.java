package com.harunabot.chatannotator.screenshot.client.handler;

import com.harunabot.chatannotator.screenshot.ScreenRecorder;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class RenderEventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	static public void onRenderWorldLastEvent(RenderWorldLastEvent event)
	{
		if (!ScreenRecorder.isShootFlagOn()) return;

		ScreenRecorder.createScreenshot();
	}
}
