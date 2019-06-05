package com.harunabot.chatannotator.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * record chat annotation logs
 */
public class AnnotationLog
{
	private final String logFilePath;

	public AnnotationLog(String logFilePath)
	{
		this.logFilePath = logFilePath;
	}

	public void writeLog()
	{
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(logFilePath, true);
			pw= new PrintWriter(new BufferedWriter(fw)) ;
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (pw != null) {
                pw.close();
            }
		}
	}
}
