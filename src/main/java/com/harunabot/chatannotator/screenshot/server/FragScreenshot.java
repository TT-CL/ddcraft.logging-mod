package com.harunabot.chatannotator.screenshot.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.client.StandbyScreenshots;

public class FragScreenshot
{
	private static final int PART_SIZE = StandbyScreenshots.PART_SIZE;

	private byte[] data;
	private int count;

	public FragScreenshot(int parts, int length)
	{
		data = new byte[length];
		count = parts;
	}

	public boolean isComplete()
	{
		return count == 0;
	}

	public void applySubData(int partId, byte[] subdata)
	{
		System.arraycopy(subdata, 0, data, partId * PART_SIZE, subdata.length);
		count--;
	}

	public void saveImage(File file)
	{
		if (!isComplete()) return;

		try{
			BufferedImage screenshot = ImageIO.read( new ByteArrayInputStream( data ) );
			ImageIO.write(screenshot, "png", file);
		}
		catch( IOException e )
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to save image data");
		}
	}
}
