package jp.ac.titech.c.cl.chatannotator.annotator.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import net.minecraft.entity.player.EntityPlayerMP;

public class AnnotationRecorder
{
	private Map<Integer, AnnotationLog> annotationLogs;

	public AnnotationRecorder()
	{
		annotationLogs = new HashMap<>();
	}

	public void addNewChat(TextComponentAnnotation component, int dimension)
	{
		AnnotationLog log = annotationLogs.get(dimension);
		if(Objects.isNull(log))
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Can't find log file for dimension " + dimension);
			return;
		}

		log.addNewChat(component);
	}

	public void annotateChat(DialogueAct annotation, int chatId, String senderId, EntityPlayerMP annotator)
	{
		int dimension = annotator.dimension;

		AnnotationLog log = annotationLogs.get(dimension);
		if(Objects.isNull(log))
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Can't find log file for dimension " + dimension);
			return;
		}

		log.annotateChat(annotation, chatId, senderId, annotator);
	}

	public void onCreateDimension(int dimension)
	{
		annotationLogs.put(dimension, new AnnotationLog(dimension));
	}

	public void onDestroyDimension(int dimension)
	{
		if (!annotationLogs.containsKey(dimension)) return;

		annotationLogs.get(dimension).outputAnnotationFile();
		annotationLogs.remove(dimension);
	}
}
