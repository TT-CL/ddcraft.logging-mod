package com.harunabot.chatannotator.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

/**
 * record chat annotation logs
 */
public class AnnotationLog
{
	private final String logFilePath;
	//private Map<String, TextComponentAnnotation> components;
	private Map<String, TextComponentAnnotation> annotatedComponents;
	private List<TextComponentAnnotation> unAnnotatedComponents;


	public AnnotationLog(String logFilePath)
	{
		this.logFilePath = logFilePath;
//		this.components = new HashMap<>();
		this.annotatedComponents = new HashMap<>();
		this.unAnnotatedComponents = new ArrayList<>();
	}

	// TODO: should write one by one
	public void addNewChat(TextComponentAnnotation component)
	{
		unAnnotatedComponents.add(component);


//		addComponent(component);
//
//		Map<String, String> outputMap = new HashMap<>();
//		for(String key: this.components.keySet())
//		{
//			outputMap.put(key, components.get(key).toLogString());
//		}
//
//		PrintWriter pw = null;
//		try {
//			FileWriter fw = new FileWriter(logFilePath, true);
//			pw= new PrintWriter(new BufferedWriter(fw)) ;
//			pw.println(outputMap.toString());
//			pw.close();
//			ChatAnnotator.LOGGER.log(Level.INFO, "Output file.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally {
//            if (pw != null) {
//                pw.close();
//            }
//		}
	}

	public void outputFile()
	{
		System.out.println(this.unAnnotatedComponents);

		Map<String, String> outputMap = new HashMap<>();
		for(String key: this.annotatedComponents.keySet())
		{
			outputMap.put(key, annotatedComponents.get(key).toLogString());
		}

		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(logFilePath, true);
			pw= new PrintWriter(new BufferedWriter(fw)) ;
			pw.println(outputMap.toString());
			pw.close();
			ChatAnnotator.LOGGER.log(Level.INFO, "Output file.");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (pw != null) {
                pw.close();
            }
		}
	}


	public void annotateChat(DialogueAct annotation, String identicalString)
	{
		System.out.println("Annoation: " + annotation);

		for(TextComponentAnnotation component: this.unAnnotatedComponents)
		{
			System.out.println(identicalString + ", " + component.toIdenticalString());
			if(component.toIdenticalString().equals(identicalString))
			{
				System.out.println("matched!");
				component.annotateByReceiver(annotation);
				this.unAnnotatedComponents.remove(component);
				this.annotatedComponents.put(component.getTime(), component);
				System.out.println(unAnnotatedComponents.toString());
				System.out.println(annotatedComponents.toString());
				break;
			}
		}
		// TODO; give score
	}
}
