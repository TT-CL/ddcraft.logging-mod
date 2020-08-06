package com.harunabot.chatannotator.annotator.server;

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
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.annotator.DialogueAct;
import com.harunabot.chatannotator.common.ChatAnnotatorHooks;
import com.harunabot.chatannotator.server.ChatData;
import com.harunabot.chatannotator.server.FileOutput;
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
	/** Unannotated chats */
	private Map<String, Map<Integer, Integer>> unAnnotatedIndices;


	public AnnotationLog(int dimension)
	{
		this.dimension = dimension;
		File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		this.logFile = new File(dimDir, CHATLOG_FILENAME);
		FileOutput.createFile(logFile);

		this.components = new ArrayList<>();
		this.unAnnotatedIndices = new HashMap<>();
	}


	public void addNewChat(TextComponentAnnotation component)
	{
		// add chat component
		int componentIndex = components.size();
		components.add(component);

		// mark component as unannotated
		String sender = component.getSender();
		int chatId = component.getNumeralId();
		if (!unAnnotatedIndices.containsKey(sender))
		{
			unAnnotatedIndices.put(sender, new HashMap<>());
		}
		unAnnotatedIndices.get(sender).put(chatId, componentIndex);

		// log
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
					component.getText(),
					component.getNumeralId());
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

	public void annotateChat(DialogueAct annotation, int chatId, String senderId, EntityPlayerMP annotator)
	{
		// get component from unannotatedChats
		Map<Integer, Integer> unannotated = unAnnotatedIndices.get(senderId);
		if (Objects.isNull(unannotated) || !unannotated.containsKey(chatId))
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to annotate chat! id: " + chatId + ", sender: " + senderId);
			return;
		}

		int componentIndex = unannotated.get(chatId);
		TextComponentAnnotation component = components.get(componentIndex);
		if (Objects.isNull(component))
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to annotate chat! id: " + chatId + ", sender: " + senderId);
			return;
		}

		// AnnotationEvent
		TextComponentAnnotation annotatedComponent = component.createCopy(); // create copy to protect original component
		annotatedComponent.annotateByReceiver(annotation);
		annotatedComponent = ChatAnnotatorHooks.onAnnotationEvent(annotatedComponent, annotator);
		if(annotatedComponent == null)
		{
			return;
		}

		component.annotateByReceiver(annotatedComponent.getReceiverAnnotation());
		unannotated.remove(chatId);

		// log
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		String output = time + " :[ANNOTATION] " + annotation.toString() +  "=>" + annotatedComponent.toLogString();
		FileOutput.appendFile(logFile, output);
	}
}
