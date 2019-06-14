package com.harunabot.chatannotator.util.handlers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class AnnotationHandler
{
	private static final String DIR_NAME = "annotationLogs";
	private static final String CHAT_KEY = "chat.type.text";

	public static AnnotationLog annotationLog;

	public static void preInit(FMLPreInitializationEvent event)
	{
		// Make output file
		// TODO: separate by world
		String date = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date());
		String filePath = DIR_NAME + "/chatLog_" + date + ".log";
		createOutputFile(DIR_NAME, filePath);

		// Initialize logger
		annotationLog = new AnnotationLog(filePath);
	}

	public static void init(FMLInitializationEvent event)
	{
	}

	public static void postInit(FMLPostInitializationEvent event)
	{
	}



	/**
	 * Change chat message to TextComponentAnnotation
	 * and take log?
	 */
	@SubscribeEvent
	public static void onServerChat(ServerChatEvent event)
	{
		ITextComponent component = event.getComponent();
		if(!(event.getComponent() instanceof TextComponentTranslation)) return;

		ITextComponent newComponent = createAnnotatedServerChat((TextComponentTranslation) component, event);

		event.setComponent(newComponent);
	}

	/**
	 * Set the proper style
	 * @param event
	 */
	@SubscribeEvent
	public static void onReceivedClientChat(ClientChatReceivedEvent event)
	{
		//TEMP
		//ChatAnnotator.LOGGER.log(Level.INFO, event.getMessage().toString());

		if(!(event.getMessage() instanceof TextComponentTranslation)) return;

		TextComponentTranslation component = (TextComponentTranslation) event.getMessage();
		// Pass non-chat message
		if(!component.getKey().equals(CHAT_KEY)) return;

		// Find Message Component
		Object[] args = component.getFormatArgs();
		TextComponentString msgComponentString = findMsgComponent(args);

		// Convert to TextComponentAnnotation
		TextComponentAnnotation msgComponent = new TextComponentAnnotation(msgComponentString);
		if(msgComponent.getTime().equals("")) return;

		// Set chat to proper style based on the sender/receiver
		UUID receiverId = Minecraft.getMinecraft().player.getUniqueID();
		msgComponent.toProperStyle(receiverId);

		args[1] = msgComponent;
		event.setMessage(new TextComponentTranslation(component.getKey(), args));
	}

	private static boolean isAnnotation(TextComponentString component)
	{
		return component.getText().startsWith("[");
	}

	private static boolean isChatTranslationComponent(ITextComponent comp)
	{
		if(!(comp instanceof TextComponentTranslation)) return false;

		TextComponentTranslation component = (TextComponentTranslation) comp;
		String key = component.getKey();
		return key == "";
	}

	/**
	 * Find chat TextComponentString from TextComponentTranslation and replaces it with TextComponentAnnotation
	 */
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ServerChatEvent event)
	{
		// Find message component
		Object[] args = component.getFormatArgs();
		TextComponentString msgComponent= findMsgComponent(args);
		if(msgComponent == null)
		{
			Main.LOGGER.log(Level.ERROR, "Can't find message component: " + component.getFormattedText());
			return component;
		}

		// Sender's UUID
		UUID senderId = event.getPlayer().getUniqueID();
		ITextComponent newMsgComponent = createAnnotatedChat(msgComponent, senderId);
		if(newMsgComponent == null) {
			// Something wrong with the message
			ChatAnnotator.LOGGER.log(Level.ERROR, "Invalid chat textcomponent: " + msgComponent.getText());
			return component;
		}

		// Set new component
		args[1] = newMsgComponent;

		return new TextComponentTranslation(component.getKey(), args);
	}

	/**
	 * Find Chat message part from TextComponentTranslation
	 */
	private static TextComponentString findMsgComponent(Object[] args)
	{
		if(args.length < 1 || !(args[1] instanceof TextComponentString))
		{
			return null;
		}

		return (TextComponentString) args[1];
	}

	/**
	 * Create new AnnotatedComponent from TextComponentString
	 */
	private static TextComponentString createAnnotatedChat(TextComponentString msgComponent, UUID senderId)
	{
		// Separate the message into the annoation part & main part
		String rawMsg = msgComponent.getText();
		String msg; //= rawMsg.substring(rawMsg.indexOf("+") + 1);
		String annotationStr;
		DialogueAct annotation;
		try
		{
			msg = rawMsg.substring(rawMsg.indexOf(">") + 1);
			annotationStr = rawMsg.substring(1,rawMsg.indexOf(">"));
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}

		// Resolve annotation
		annotation = DialogueAct.convertFromName(annotationStr);
		if (annotation == null)
		{
			return null;
		}

		TextComponentAnnotation annotatedChat = new TextComponentAnnotation(msg, annotation, senderId);
		// Take log
		writeLog(annotatedChat);

		return annotatedChat.toComponentString();
	}

	public static void onAnnotatedChat()
	{

	}

	/**
	 * Take log
	 */
	protected static void writeLog(TextComponentAnnotation component)
	{
		try
		{
			annotationLog.writeLog((TextComponentAnnotation) component);
		}
		catch(NullPointerException e)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Can't find log file");
		}
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
            ChatAnnotator.LOGGER.log(Level.INFO, "Successfully created output file");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
