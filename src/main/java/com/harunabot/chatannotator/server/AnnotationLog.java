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
import com.harunabot.chatannotator.annotator.DialogueAct;
import com.harunabot.chatannotator.common.ChatAnnotatorHooks;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * record chat annotation logs
 */
public class AnnotationLog
{
	public static final String CHATLOG_FILENAME = "chatLog.log";
	public static final String ANNOTATION_FILENAME = "annotation.json";

	private final int dimension;
	private final File logFile;

	/** All Chats */
	private List<TextComponentAnnotation> components;
	/** Annotated chats */
	private Map<String, TextComponentAnnotation> annotatedComponents;
	/** Unannotated chats */
	private List<TextComponentAnnotation> unAnnotatedComponents;


	public AnnotationLog(int dimension)
	{
		this.dimension = dimension;
		File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		this.logFile = new File(dimDir, CHATLOG_FILENAME);
		FileOutput.createFile(logFile);

		this.components = new ArrayList<>();
		this.annotatedComponents = new HashMap<>();
		this.unAnnotatedComponents = new ArrayList<>();
	}


	public void addNewChat(TextComponentAnnotation component)
	{
		components.add(component);
		unAnnotatedComponents.add(component);

		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		String output = time + " :[CHAT] " + component.toLogString();
		FileOutput.appendFile(logFile, output);
	}

	public void outputAnnotationFile()
	{
		Map<String, List<ChatData>> outputMap = new HashMap<>();
		for(TextComponentAnnotation component: components)
		{
			String key = component.getTime();
			ChatData chatData = new ChatData(
					component.getSenderAnnotation(),
					component.getReceiverAnnotation(),
					component.getSender(),
					component.getTime(),
					component.getText());
			if(!outputMap.containsKey(key))
			{
				outputMap.put(key, new ArrayList<>());
			}
			outputMap.get(key).add(chatData);
		}

		File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		File jsonFile = new File(dimDir, ANNOTATION_FILENAME);
		FileOutput.outputJson(jsonFile, outputMap);
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
				String output = time + " :[ANNOTATION] " + annotation.toString() +  "=>" + annotatedComponent.toLogString();
				FileOutput.appendFile(logFile, output);

				break;
			}
		}
	}
}
