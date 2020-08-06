package com.harunabot.chatannotator.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harunabot.chatannotator.ChatAnnotator;

public class FileOutput
{
	public static boolean outputJson(File jsonFile, Object jsonValue)
	{
		boolean isNew = !jsonFile.exists();
		ObjectMapper mapper = new ObjectMapper();

		try {
			 mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, jsonValue);
		} catch (Exception e) {
			e.printStackTrace();
	    	ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to output json");
			return false;
		}

		if (isNew)
		{
			ChatAnnotator.LOGGER.log(Level.INFO, "Created file: " + jsonFile.toString());
		}
		return true;
	}

	public static boolean appendFile(File file, String output)
	{
		PrintWriter pw = null;
		boolean result = true;

		if (!file.exists())
		{
			createFile(file);
		}

		try {
			FileWriter fw = new FileWriter(file, true);
			pw= new PrintWriter(new BufferedWriter(fw)) ;
			pw.println(output);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to output file");
			result = false;
		}finally {
            if (pw != null) {
                pw.close();
            }
		}

		return result;
	}

	public static boolean createFile(File file)
	{
        try {
            file.createNewFile();
            ChatAnnotator.LOGGER.log(Level.INFO, "Created file: " + file.toString());
        } catch (IOException e) {
            e.printStackTrace();
            ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to create file: " + file.toString());
            return false;
        }

        return true;
	}
}
