package com.harunabot.chatannotator.util.handlers;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.util.text.StringTools;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
/**
 * Makes chat annotatable & visible only for same dimension's players
 */
public class ChatEventHandler
{
	private static final String CHAT_KEY = "chat.type.text";

	@SubscribeEvent
	public static void onStartGame(WorldEvent.Load event)
	{
		World world = event.getWorld();
		if (world.isRemote) return;

		int dimension = world.provider.getDimension();
		if (dimension == 1 || dimension == -1)
		{
			// No log for nether and The end
			return;
		}
		ChatAnnotator.annotationLogs.put(dimension, new AnnotationLog(dimension));
	}

	@SubscribeEvent
	public static void onFinishGame(WorldEvent.Unload event)
	{
		World world = event.getWorld();
		int dimension = world.provider.getDimension();
		Map<Integer, AnnotationLog> logs = ChatAnnotator.annotationLogs;
		if (world.isRemote || !logs.containsKey(dimension)) return;

		logs.get(dimension).outputAnnotationFile();
		logs.remove(dimension);
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
		// Sender's dimension
		int dimension = event.getPlayer().getServerWorld().provider.getDimension();

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
			ITextComponent newComponent = createAnnotatedServerChat((TextComponentTranslation) component, elements, senderId, dimension);
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
	 * Set the proper style, make chat from other dimension invisible
	 * @param event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onReceivedClientChat(ClientChatReceivedEvent event)
	{
		if(!(event.getMessage() instanceof TextComponentTranslation)) return;
		EntityPlayer player = Minecraft.getMinecraft().player;

		TextComponentTranslation component = (TextComponentTranslation) event.getMessage();
		// Pass non-chat message
		if(!component.getKey().equals(CHAT_KEY)) return;

		// Find Message Component
		Object[] args = component.getFormatArgs();
		TextComponentString msgComponentString = findMsgComponent(args);

		// Convert to TextComponentAnnotation
		TextComponentAnnotation msgComponent = new TextComponentAnnotation(msgComponentString);
		if(msgComponent.getTime().equals("")) return;

		// Check Dimension and return if different dimension
		int messageDim = msgComponent.getDimension();
		int playerDim = player.world.provider.getDimension();
		if (messageDim != playerDim)
		{
			event.setCanceled(true);
			return;
		}

		// Set chat to proper style based on the sender/receiver
		UUID receiverId = player.getUniqueID();
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
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ComponentElements elements, UUID senderId, int dimension)
	{
		ITextComponent newMsgComponent = createAnnotatedChat(elements.msgComponent, senderId, dimension);
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
	private static TextComponentString createAnnotatedChat(TextComponentString msgComponent, UUID senderId, int dimension)
	{
		// Separate the message into the annotation part & main part
		String rawMsg = msgComponent.getText();
		Pair<String, String> separatedMsg = StringTools.separatePrefixBySymbols(rawMsg, '<', '>');
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

		TextComponentAnnotation annotatedChat = new TextComponentAnnotation(msg, annotation, senderId, dimension);

		// Take log
		addNewChat(annotatedChat, dimension);

		return annotatedChat.toComponentString();
	}

	public static void onAnnotatedChat(String rawMsg, EntityPlayerMP player)
	{
		Pair<String, String> separatedMsg = StringTools.separatePrefixBySymbols(rawMsg, '[', ']');
		Pair<String, String> separatedRight = StringTools.separatePrefixBySymbols(separatedMsg.getRight(), '(', ')');
		String annotationStr = separatedMsg.getLeft();
		int dimension = Integer.valueOf(separatedRight.getLeft());
		String identicalString = separatedRight.getRight();

		DialogueAct annotation = DialogueAct.convertFromName(annotationStr);
		if (annotation == null)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Something wrong with the annotation msg: " + rawMsg);
			return;
		}

		ChatAnnotator.annotationLogs.get(dimension).annotateChat(annotation, identicalString, player);
	}

	/**
	 * Take log
	 */
	protected static void addNewChat(TextComponentAnnotation component, int dimension)
	{
		try
		{
			ChatAnnotator.annotationLogs.get(dimension).addNewChat((TextComponentAnnotation) component);
		}
		catch(NullPointerException e)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Can't find log file");
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
