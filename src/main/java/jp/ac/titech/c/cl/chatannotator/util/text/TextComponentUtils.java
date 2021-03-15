package jp.ac.titech.c.cl.chatannotator.util.text;

import java.util.UUID;

import javax.xml.soap.Text;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import jp.ac.titech.c.cl.chatannotator.common.config.ModConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TextComponentUtils
{
	private static final String CHAT_KEY = "chat.type.text";


	/**
	 * Create new AnnotatedComponent from TextComponentString
	 */
	public static TextComponentString createAnnotatedChat(TextComponentString msgComponent, UUID senderId, int dimension, int numeralId)
	{
		// Separate the message into the annotation part & main part
		String rawMsg = msgComponent.getText();
		Pair<String, String> separatedMsg = StringTools.separatePrefixBySymbols(rawMsg, '<', '>');
		String annotationIdString = separatedMsg.getLeft();
		String msg = separatedMsg.getRight();

		TextComponentAnnotation component;
		if (ModConfig.isAnnotationEnabled() && !annotationIdString.isEmpty())
		{
			// Create annotatable component
			int annotationId = Integer.parseInt(annotationIdString);
			DialogueAct annotation;

			// Resolve annotation
			annotation = DialogueAct.convertFromId(annotationId);
			if (annotation == null)
			{
				ChatAnnotator.LOGGER.log(Level.ERROR, "Something wrong with the chat msg: " + rawMsg);
				return null;
			}

			component = new TextComponentAnnotation(msg, annotation, senderId, dimension, numeralId);
		}
		else
		{
			// Create non-annotatable component
			component = new TextComponentAnnotation(msg, DialogueAct.NO_ANNOTATION, senderId, dimension, numeralId);
			component.annotateByReceiver(DialogueAct.NO_ANNOTATION);
		}

		// Take log
		ChatAnnotator.ANNOTATION_RECORDER.addNewChat(component, dimension);

		return component.toComponentString();
	}

	/**
	 * Create AnnotatedComponent from existing ITextComponent
	 * @return TextComponentAnnotation of component if it's "annotated", else null
	 */
	public static TextComponentAnnotation getComponentAnnotation(ITextComponent icomponent)
	{
		if(!(icomponent instanceof TextComponentTranslation)) return null;
		TextComponentTranslation component = (TextComponentTranslation) icomponent;

		// Pass non-chat message
		if(!component.getKey().equals(CHAT_KEY)) return null;

		// Find Message Component
		Object[] args = component.getFormatArgs();
		TextComponentString msgComponentString = findMsgComponent(args);

		// Convert to TextComponentAnnotation
		if(!TextComponentAnnotation.isInterpretable(msgComponentString)) return null;
		TextComponentAnnotation annotationComponent = new TextComponentAnnotation(msgComponentString);

		return annotationComponent;
	}

	/**
	 * Revert AnnotatedComponent to TextComponentString
	 */
	public static TextComponentString revertAnnotatedChat(TextComponentAnnotation component)
	{
		return null;
	}


	/**
	 * Find Chat message part from TextComponentTranslation
	 */
	public static TextComponentString findMsgComponent(Object[] args)
	{
		if(args.length < 1 || !(args[1] instanceof TextComponentString))
		{
			return null;
		}

		return (TextComponentString) args[1];
	}
}
