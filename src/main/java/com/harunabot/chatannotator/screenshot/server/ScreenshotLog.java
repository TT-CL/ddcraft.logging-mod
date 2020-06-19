package com.harunabot.chatannotator.screenshot.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.client.StandbyScreenshots;

import scala.language;
import scala.collection.generic.BitOperations.Int;

public class ScreenshotLog
{
	public static final int PART_SIZE = StandbyScreenshots.PART_SIZE;

	private class Screenshot
	{
		byte[] data;
		int count;

		public Screenshot(int parts, int length)
		{
			data = new byte[length];
			count = parts;
		}

		public void applySubData(int id, byte[] subdata)
		{
			System.arraycopy(subdata, 0, data, id * PART_SIZE, subdata.length);
			count--;
		}

		public void saveImage()
		{
			if (count > 0) return;

			try{
				BufferedImage screenshot = ImageIO.read( new ByteArrayInputStream( data ) );
				ImageIO.write(screenshot, "png", new File(ChatAnnotator.modDirectory, "test.png"));
			}
			catch( IOException e )
			{
				ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to save image data");
			}
		}
	}

	// TODO: reserve for each players
	// TODO: チャット内容とimageIdの紐付け
	private Map<Integer, Screenshot> screenshots = new HashMap<Integer, Screenshot>();

	public int reserveScreenshot(int imageId, int parts, int length)
	{
		screenshots.put(imageId, new Screenshot(parts, length));
		return imageId;
	}

	public void saveSubData(int imageId, int partId, byte[] subData)
	{
		Screenshot image = screenshots.get(imageId);
		image.applySubData(partId, subData);

		// Output as an image if all the data arrives, then release
		if (image.count > 0) return;
		image.saveImage();
		screenshots.remove(imageId);
	}
}
