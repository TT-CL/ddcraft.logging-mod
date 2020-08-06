package com.harunabot.chatannotator.util.handlers.event;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class WorldEventHandler
{
	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public static void onStartGame(WorldEvent.Load event)
	{
		World world = event.getWorld();
		if (world.isRemote) return;

		int dimension = world.provider.getDimension();
		if (dimension == 1 || dimension == -1)
		{
			// No log for nether and The end
			return;
		}

		// dimension directory
		String dirname = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date()) + "_" + dimension;
		File dimDir = new File(ChatAnnotator.modDirectory, dirname);
		dimDir.mkdir();
		ChatAnnotator.dimensionDirectories.put(dimension, dimDir);

		// create dimension data
		ScreenRecorder.SCREENSHOT_LOG.onCreateDimension(dimension);
		ChatAnnotator.CHAT_RECORDER.onCreateDimension(dimension);
		ChatAnnotator.ANNOTATION_RECORDER.onCreateDimension(dimension);
		ChatAnnotator.CHAT_ID_MANAGER_SERVER.onCreateDimension(dimension);
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public static void onFinishGame(WorldEvent.Unload event)
	{
		World world = event.getWorld();
		int dimension = world.provider.getDimension();
		if (world.isRemote) return;
		if (dimension == 1 || dimension == -1)
		{
			// No log for nether and The end
			return;
		}

		// refresh dimension data
		ScreenRecorder.SCREENSHOT_LOG.onDestroyDimension(dimension);
		ChatAnnotator.CHAT_RECORDER.onDestroyDimension(dimension);
		ChatAnnotator.ANNOTATION_RECORDER.onDestroyDimension(dimension);
		ChatAnnotator.CHAT_ID_MANAGER_SERVER.onDestroyDimension(dimension);

		// Dimension directory
		ChatAnnotator.dimensionDirectories.remove(dimension);
	}
}
