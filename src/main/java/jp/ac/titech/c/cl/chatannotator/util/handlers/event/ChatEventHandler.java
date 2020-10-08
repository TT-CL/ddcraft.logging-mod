package jp.ac.titech.c.cl.chatannotator.util.handlers.event;

import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import jp.ac.titech.c.cl.chatannotator.common.config.AnnotationConfig;
import jp.ac.titech.c.cl.chatannotator.logger.network.PlayerStateMessage;
import jp.ac.titech.c.cl.chatannotator.network.ChatIdMessage;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import jp.ac.titech.c.cl.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
import jp.ac.titech.c.cl.chatannotator.util.text.StringTools;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
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

	/** =================
	 *    SERVER SIDE
	 * ================== */

	/**
	 * Change chat message to TextComponentAnnotation and take log
	 */
	@SubscribeEvent
	public static void onServerChat(ServerChatEvent event)
	{
		ITextComponent component = event.getComponent();
		ChatEventHandler.ComponentElements elements = validateServerChat(component);
		if(elements == null) return;

		// Sender's info
		EntityPlayerMP player = event.getPlayer();
		UUID senderId = player.getUniqueID();
		int dimension = player.dimension;

		String msg = elements.msgComponent.getText();
		int numeralId = ChatAnnotator.CHAT_ID_MANAGER_SERVER.getIdOnServerChat(msg, player);

		// Replace Chat
		ITextComponent newComponent = createAnnotatedServerChat((TextComponentTranslation) component, elements, senderId, dimension, numeralId);
		event.setComponent(newComponent);
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

	/** =================
	 *    CLIENT SIDE
	 * ================== */

	/**
	 * Send annotation packets together with chat
	 * @param event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientChat(ClientChatEvent event)
	{
		String msg = event.getMessage();
		if (msg.startsWith("/"))
		{
			// ignore command
			return;
		}

		String chatId = ChatAnnotator.CHAT_ID_MANAGER_CLIENT.getId(msg);

		Minecraft mc = Minecraft.getMinecraft();
		ChatAnnotatorPacketHandler.sendToServer(new ChatIdMessage(chatId, msg));
		ChatAnnotatorPacketHandler.sendToServer(new PlayerStateMessage(chatId, mc.player, mc.world, mc.getRenderPartialTicks()));

    	// send screenshot together
    	ScreenRecorder.reserveScreenshot(chatId);

		ChatAnnotator.CHAT_ID_MANAGER_CLIENT.onSendChatMessage(msg);
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

	/** =================
	 *    UTILS
	 * ================== */

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
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ComponentElements elements, UUID senderId, int dimension, int numeralId)
	{
		ITextComponent newMsgComponent = createAnnotatedChat(elements.msgComponent, senderId, dimension, numeralId);
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
	private static TextComponentString createAnnotatedChat(TextComponentString msgComponent, UUID senderId, int dimension, int numeralId)
	{
		// Separate the message into the annotation part & main part
		String rawMsg = msgComponent.getText();
		Pair<String, String> separatedMsg = StringTools.separatePrefixBySymbols(rawMsg, '<', '>');
		String annotationIdString = separatedMsg.getLeft();
		String msg = separatedMsg.getRight();

		TextComponentAnnotation component;
		if (AnnotationConfig.enableAnnotationLabel && !annotationIdString.isEmpty())
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
			System.out.println(component.toLogString());
		}

		// Take log
		ChatAnnotator.ANNOTATION_RECORDER.addNewChat(component, dimension);

		return component.toComponentString();
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
