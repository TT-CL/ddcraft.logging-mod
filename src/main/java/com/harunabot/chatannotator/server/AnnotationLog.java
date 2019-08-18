package com.harunabot.chatannotator.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.common.ChatAnnotatorHooks;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * record chat annotation logs
 */
public class AnnotationLog
{
	protected static final String DIR_NAME = "annotationLogs/";
	protected static final String CHATLOG_FNAME_FORMAT = "chatLog_%s_%d.log";
	protected static final String ANNOTATION_FNAME_FORMAT= "annotation_%s_%d.json";

	private final String logFilePath;
	private final String annotationFilePath;
	/** All Chats */
	private List<TextComponentAnnotation> components;
	/** Annotated chats */
	private Map<String, TextComponentAnnotation> annotatedComponents;
	/** Unannotated chats */
	private List<TextComponentAnnotation> unAnnotatedComponents;


	public AnnotationLog(int dimension)
	{
		String date = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date());
		this.logFilePath = DIR_NAME + String.format(CHATLOG_FNAME_FORMAT, date, dimension);
		this.annotationFilePath = DIR_NAME + String.format(ANNOTATION_FNAME_FORMAT, date, dimension);

		this.components = new ArrayList<>();
		this.annotatedComponents = new HashMap<>();
		this.unAnnotatedComponents = new ArrayList<>();

		// Make output file
		createOutputFile(DIR_NAME, logFilePath);
	}

	/**
	 * Create output file for log
	 */
	protected static void createOutputFile(String directoryName, String filePath)
	{
		File dir = new File(directoryName);
        File file = new File(filePath);
        try {
        	if(!dir.exists()) dir.mkdir();
            file.createNewFile();
            ChatAnnotator.LOGGER.log(Level.INFO, "Successfully created output file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void addNewChat(TextComponentAnnotation component)
	{
		components.add(component);
		unAnnotatedComponents.add(component);

		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		String output = time + " :[CHAT] " + component.toLogString();
		writeFile(output, logFilePath, "");
	}

	public void outputAnnotationFile()
	{
		File outputFile = new File(annotationFilePath);
		if(!outputFile.exists())
		{
			createOutputFile(DIR_NAME, annotationFilePath);
		}

		Map<String, List<String>> outputMap = new HashMap<>();
		for(TextComponentAnnotation component: components)
		{
			String key = component.getTime();
			if(!outputMap.containsKey(key))
			{
				outputMap.put(key, new ArrayList<>());
			}
			outputMap.get(key).add(component.toLogString());
		}

		writeFile(outputMap.toString(), annotationFilePath, "Output file.");
	}


	@Nullable
	public void annotateChat(DialogueAct annotation, String identicalString, EntityPlayerMP player)
	{
		for(TextComponentAnnotation component: this.unAnnotatedComponents)
		{
			if(component.toIdenticalString().equals(identicalString))
			{
				// AnnotationEvent
				TextComponentAnnotation annotatedComponent = component.createCopy(); // create copy to protect original component
				annotatedComponent.annotateByReceiver(annotation);
				annotatedComponent = ChatAnnotatorHooks.onAnnotationEvent(annotatedComponent, player);
				if(annotatedComponent == null)
				{
					return;
				}

				component.annotateByReceiver(annotatedComponent.getReceiverAnnotation());
				this.unAnnotatedComponents.remove(component);
				this.annotatedComponents.put(component.getTime(), component);

				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				String output = time + " :[ANNOTATION] " + annotation.getName() +  "=>" + annotatedComponent.toLogString();
				writeFile(output, logFilePath, "");

				break;
			}
		}
	}

	protected void writeFile(String output, String filePath, String successLog)
	{
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(filePath, true);
			pw= new PrintWriter(new BufferedWriter(fw)) ;
			pw.println(output);
			pw.close();
			if(!successLog.isEmpty())
			{
				ChatAnnotator.LOGGER.log(Level.INFO, successLog);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (pw != null) {
                pw.close();
            }
		}
	}
}
