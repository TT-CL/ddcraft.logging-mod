package com.harunabot.chatannotator.screenshot.server;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.harunabot.chatannotator.ChatAnnotator;

import net.minecraft.entity.player.EntityPlayerMP;

public class ScreenshotLog
{
	// TODO: チャット内容とimageIdの紐付け
	private Map<Integer, Map<UUID, Map<String, FragScreenshot>>> dimensionShots = new HashMap<>();

	public void onCreateDimension(int dimension)
	{
		dimensionShots.put(dimension, new HashMap<>());
	}

	public void onDestroyDimension(int dimension)
	{
		if (!dimensionShots.containsKey(dimension)) return;

		dimensionShots.remove(dimension);
		// TODO: log if something left?
	}

	// register new FragScreenshot to wait for fragments of the screenshot
	public void reserveScreenshot(EntityPlayerMP player, String serialId, int parts, int length)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<String, FragScreenshot>> playerShots = dimensionShots.get(dimension);
		if (!playerShots.containsKey(uuid))
		{
			playerShots.put(uuid, new HashMap<>());
		}

		Map<String, FragScreenshot> screenshots = playerShots.get(uuid);
		screenshots.put(serialId, new FragScreenshot(parts, length));

		return;
	}

	public void saveSubData(EntityPlayerMP player, String serialId, int partId, byte[] subData)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<String, FragScreenshot> screenshots = dimensionShots.get(dimension).get(uuid);
		FragScreenshot image = screenshots.get(serialId);
		image.applySubData(partId, subData);

		// Output as an image if all the data arrives, then release
		if (!image.isComplete()) return;

		File dimDir = ChatAnnotator.dimensionDirectories.get(player.dimension);
		File outputDir = new File(dimDir, uuid.toString());
		if (!outputDir.exists()) outputDir.mkdir();

		int numeralId = ChatAnnotator.CHAT_ID_MANAGER_SERVER.getId(player, serialId);
		String date = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date());
		String fileName = String.format("%03d_%s.png", numeralId, date);
		image.saveImage(new File(outputDir, fileName));
		screenshots.remove(serialId);
	}
}
