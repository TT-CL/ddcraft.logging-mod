package com.harunabot.chatannotator.util.handlers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.Main;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

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
		System.out.println(newComponent.toString());
	}

	/**
	 * Set the proper style
	 * @param event
	 */

	// TODO: 置き換えると何故かStringになってしまっている
	// 置き換えない場合どうなっているか確認して直す
	// 最悪サーバー側に保存して引っ張ってくるとか？
	@SubscribeEvent
	public static void onReceivedClientChat(ClientChatReceivedEvent event)
	{
		/*
		Main.LOGGER.log(Level.INFO, event.getMessage().toString());
		event.setMessage(event.getMessage().setStyle(new Style().setColor(TextFormatting.UNDERLINE)));


		ITextComponent component = event.getMessage();
		if(!(component instanceof TextComponentTranslation)) return;

		// Find Message Component
		Object[] args = ((TextComponentTranslation) component).getFormatArgs();
		Object msgObject = args[1];
		if(!(msgObject instanceof TextComponentAnnotation))
		{
			Main.LOGGER.log(Level.ERROR, "Can't find message component on client: " + component.toString());
			Main.LOGGER.log(Level.ERROR, "Can't find message component on client: " + msgObject.toString());
			return;
		}

		// Set chat to proper style based on the sender/receiver
		TextComponentAnnotation msgComponent = (TextComponentAnnotation) msgObject;
		UUID receiverId = Minecraft.getMinecraft().player.getUniqueID();
		msgComponent.toProperStyle(receiverId);
		*/
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
			Main.LOGGER.log(Level.ERROR, "Invalid chat textcomponent: " + msgComponent.getText());
			return component;
		}

		// Set new component
		args[1] = newMsgComponent;

		System.out.println(args.toString());

		return new TextComponentTranslation(component.getKey(), args);
	}

	/**
	 * Find Chat message part from TextComponentTranslation
	 */
	private static TextComponentString findMsgComponent(Object[] args)
	{
		Object msgObject = args[1];
		if(!(msgObject instanceof TextComponentString))
		{
			return null;
		}

		return (TextComponentString) msgObject;
	}

	/**
	 * Create new TextComponentAnnotation from TextComponentString
	 */
	private static TextComponentAnnotation createAnnotatedChat(TextComponentString msgComponent, UUID senderId)
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

		return annotatedChat;
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
			Main.LOGGER.log(Level.ERROR, "Can't find log file");
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
            Main.LOGGER.log(Level.INFO, "Successfully created output file");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
