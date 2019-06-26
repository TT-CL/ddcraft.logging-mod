package com.harunabot.chatannotator.util.handlers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.util.text.StringTools;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChatEventHandler
{
	private static final String CHAT_KEY = "chat.type.text";

	public static AnnotationLog annotationLog = null;

	public static void preInit(FMLPreInitializationEvent event)
	{
		// Initialize logger
		annotationLog = new AnnotationLog();
	}

	public static void init(FMLInitializationEvent event)
	{
	}

	public static void postInit(FMLPostInitializationEvent event)
	{
	}

	@SubscribeEvent
	public static void onFinishGame(WorldEvent.Unload event)
	{
		if(event.getWorld().isRemote | annotationLog == null) return;

		annotationLog.outputAnnotationFile();
	}

	/**
	 * Change chat message to TextComponentAnnotation
	 * or apply annotation
	 * and take log
	 */
	@SubscribeEvent
	public static void onServerChat(ServerChatEvent event)
	{
		ITextComponent component = event.getComponent();
		ChatEventHandler.ComponentElements elements = validateServerChat(component);
		if(elements == null) return;

		// Sender's UUID
		UUID senderId = event.getPlayer().getUniqueID();

		if(isAnnotation(elements.msgComponent))
		{
			// Annotate
			onAnnotatedChat(elements.msgComponent.getText(), event.getPlayer());
			event.setCanceled(true);
			return;
		}
		else
		{
			// Replace Chat
			ITextComponent newComponent = createAnnotatedServerChat((TextComponentTranslation) component, elements, senderId);
			event.setComponent(newComponent);
		}
	}

	/**
	 * Checks if serverEvent is valid for annotation chat
	 * @return ComponentElements if valid component
	 */
	private static ComponentElements validateServerChat(ITextComponent component)
	{
		// ComponentTranlation?
		if(!(component instanceof TextComponentTranslation)) return null;

		// Has message?
		Object[] args = ((TextComponentTranslation) component).getFormatArgs();
		TextComponentString msgComponent= findMsgComponent(args);
		if(msgComponent == null)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Can't find message component: " + component.getFormattedText());
			return null;
		}

		return new ChatEventHandler.ComponentElements(args, msgComponent);
	}

	/**
	 * Set the proper style
	 * @param event
	 */
	@SubscribeEvent
	public static void onReceivedClientChat(ClientChatReceivedEvent event)
	{
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
	 * Create new componentTranslation with annotated message
	 */
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ComponentElements elements, UUID senderId)
	{
		ITextComponent newMsgComponent = createAnnotatedChat(elements.msgComponent, senderId);
		if(newMsgComponent == null) {
			// Something wrong with the message
			ChatAnnotator.LOGGER.log(Level.ERROR, "Invalid chat textcomponent: " + elements.msgComponent.getText());
			return component;
		}

		// Set new component
		elements.args[1] = newMsgComponent;

		return new TextComponentTranslation(component.getKey(), elements.args);
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
		// Separate the message into the annotation part & main part
		String rawMsg = msgComponent.getText();
		Pair<String, String> separatedMsg = StringTools.separateBySymbols(rawMsg, '<', '>');
		String annotationStr = separatedMsg.getLeft();
		String msg = separatedMsg.getRight();
		DialogueAct annotation;

		// Resolve annotation
		annotation = DialogueAct.convertFromName(annotationStr);
		if (annotation == null)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Something wrong with the chat msg: " + rawMsg);
			return null;
		}

		TextComponentAnnotation annotatedChat = new TextComponentAnnotation(msg, annotation, senderId);

		// Take log
		addNewChat(annotatedChat);

		return annotatedChat.toComponentString();
	}

	public static void onAnnotatedChat(String rawMsg, EntityPlayerMP player)
	{
		Pair<String, String> separatedMsg = StringTools.separateBySymbols(rawMsg, '[', ']');
		String annotationStr = separatedMsg.getLeft();
		String identicalString = separatedMsg.getRight();

		DialogueAct annotation = DialogueAct.convertFromName(annotationStr);
		if (annotation == null)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Something wrong with the annotation msg: " + rawMsg);
			return;
		}

		annotationLog.annotateChat(annotation, identicalString, player);
	}

	/**
	 * Take log
	 */
	protected static void addNewChat(TextComponentAnnotation component)
	{
		try
		{
			annotationLog.addNewChat((TextComponentAnnotation) component);
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

	/*==========================================================*/

	/**
	 * For extracting necessary elements from component
	 */
	private static class ComponentElements
	{
		Object[] args;
		TextComponentString msgComponent;

		public ComponentElements(Object[] args, TextComponentString msgComponent)
		{
			this.args = args;
			this.msgComponent = msgComponent;
		}
	}
}
