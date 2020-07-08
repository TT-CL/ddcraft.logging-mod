package com.harunabot.chatannotator.screenshot.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.client.StandbyScreenshots;

import net.minecraft.entity.player.EntityPlayerMP;

public class ScreenshotLog
{
	// TODO: チャット内容とimageIdの紐付け
	private Map<UUID, Map<Integer, FragScreenshot>> playerScreenshots = new HashMap<UUID, Map<Integer, FragScreenshot>>();

	public int reserveScreenshot(EntityPlayerMP player, int imageId, int parts, int length)
	{
		UUID uuid = player.getUniqueID();
		if (!playerScreenshots.containsKey(uuid))
		{
			playerScreenshots.put(uuid, new HashMap<Integer, FragScreenshot>());
		}

		Map<Integer, FragScreenshot> screenshots = playerScreenshots.get(uuid);
		screenshots.put(imageId, new FragScreenshot(parts, length));
		return imageId;
	}

	public void saveSubData(EntityPlayerMP player, int imageId, int partId, byte[] subData)
	{
		UUID uuid = player.getUniqueID();
		Map<Integer, FragScreenshot> screenshots = playerScreenshots.get(uuid);
		FragScreenshot image = screenshots.get(imageId);
		image.applySubData(partId, subData);

		// Output as an image if all the data arrives, then release
		if (!image.isComplete()) return;

		File dimDir = ChatAnnotator.dimensionDirectories.get(player.dimension);
		File outputDir = new File(dimDir, uuid.toString());
		if (!outputDir.exists()) outputDir.mkdir();

		String date = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date());
		String fileName = String.format("%03d_%s.png", imageId, date);
		image.saveImage(new File(outputDir, fileName));
		screenshots.remove(imageId);
	}
}
