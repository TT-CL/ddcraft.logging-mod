package com.harunabot.chatannotator.util.handlers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.server.AnnotationLog;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class WorldEventHandler
{
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

		// AnnotationLog
		ChatAnnotator.annotationLogs.put(dimension, new AnnotationLog(dimension));

		ScreenRecorder.SCREENSHOT_LOG.onCreateDimension(dimension);
		ChatAnnotator.CHAT_RECORDER.onCreateDimension(dimension);
	}

	@SubscribeEvent
	public static void onFinishGame(WorldEvent.Unload event)
	{
		World world = event.getWorld();
		int dimension = world.provider.getDimension();
		Map<Integer, AnnotationLog> logs = ChatAnnotator.annotationLogs;
		if (world.isRemote || !logs.containsKey(dimension)) return;

		// refresh screenshots
		ScreenRecorder.SCREENSHOT_LOG.onDestroyDimension(dimension);
		ChatAnnotator.CHAT_RECORDER.onDestroyDimension(dimension);

		// Annotation logs
		logs.get(dimension).outputAnnotationFile();
		logs.remove(dimension);

		// Dimension directory
		ChatAnnotator.dimensionDirectories.remove(dimension);
	}
}
