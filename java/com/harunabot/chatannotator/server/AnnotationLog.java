package com.harunabot.chatannotator.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.Main;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

/**
 * record chat annotation logs
 */
public class AnnotationLog
{
	private final String logFilePath;
	private Map<String, TextComponentAnnotation> components;


	public AnnotationLog(String logFilePath)
	{
		this.logFilePath = logFilePath;
		this.components = new HashMap<>();
	}

	// TODO: should write one by one
	public void writeLog(TextComponentAnnotation component)
	{
		addComponent(component);

		Map<String, String> outputMap = new HashMap<>();
		for(String key: this.components.keySet())
		{
			outputMap.put(key, components.get(key).toLogString());
		}

		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(logFilePath, true);
			pw= new PrintWriter(new BufferedWriter(fw)) ;
			pw.println(outputMap.toString());
			pw.close();
			Main.LOGGER.log(Level.INFO, "Output file.");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (pw != null) {
                pw.close();
            }
		}
	}

	private void addComponent(TextComponentAnnotation component)
	{
		components.put(component.getTime(), component);
	}
}
