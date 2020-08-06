package com.harunabot.chatannotator.screenshot.client;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.logging.log4j.Level;

import com.google.common.primitives.Bytes;
import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.screenshot.network.NotifyArrivalMessage;
import com.harunabot.chatannotator.screenshot.network.ScreenshotDataMessage;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Hash;
import scala.reflect.api.Internals.ReificationSupportApi.SyntacitcSingletonTypeExtractor;

public class StandbyScreenshots
{
	public static final int PART_SIZE = 30000;

	private Map<String, byte[]> imageBytes;

	public StandbyScreenshots()
	{
		imageBytes = new HashMap<>();
	}

	/**
	 * Convert BufferedImage to byte array, store them and notify server
	 */
	public NotifyArrivalMessage registerImage(String serialId, BufferedImage screenshot)
	{
		byte[] data;

		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    BufferedOutputStream os = new BufferedOutputStream(bos);)
		{
			ImageIO.write( screenshot, "png", os );
			data = bos.toByteArray();
		}
		catch( IOException e )
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to register screenshot.");
			return new NotifyArrivalMessage("", 0, 0);
		}

		int length = data.length;
		int parts = (int)(Math.ceil(length/(double)PART_SIZE));
		imageBytes.put(serialId, data);

		return new NotifyArrivalMessage(serialId, parts, length);
	}

	public void sendSubScreenShotMessages(String serialId)
	{
		byte[] data = imageBytes.get(serialId);
		int parts = (int)(Math.ceil(data.length/(double)PART_SIZE));

        int from, to;
        for (int i = 0; i < parts; i++)
        {
        	from = i * PART_SIZE;
            to = Math.min(from + PART_SIZE, data.length);
            byte[] subdata = Arrays.copyOfRange(data, from, to);

            ScreenshotDataMessage message = new ScreenshotDataMessage(serialId, i, subdata);
            ChatAnnotatorPacketHandler.sendToServer(message);
        }

        // release
        imageBytes.remove(serialId);
	}
}
