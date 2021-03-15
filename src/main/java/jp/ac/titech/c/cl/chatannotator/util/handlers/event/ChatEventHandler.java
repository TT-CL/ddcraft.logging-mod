package jp.ac.titech.c.cl.chatannotator.util.handlers.event;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;
import jp.ac.titech.c.cl.chatannotator.common.config.ModConfig;
import jp.ac.titech.c.cl.chatannotator.logger.network.PlayerStateMessage;
import jp.ac.titech.c.cl.chatannotator.network.ChatIdMessage;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import jp.ac.titech.c.cl.chatannotator.util.handlers.ChatAnnotatorPacketHandler;
import jp.ac.titech.c.cl.chatannotator.util.text.StringTools;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
		BlockPos playerPos = player.getPosition();
		Vec3d playerLook = player.getLookVec();

		// Receiver's info (instant world only)
		BlockPos partnerPos = null;
		Vec3d partnerLook = null;
		if (dimension >= 100)
		{
			List<EntityPlayer> players = player.getServerWorld().playerEntities;
			if (players.size() == 2) //something wrong if not 2
			{
				for (EntityPlayer partner : players)
				{
					if (partner.getUniqueID() == senderId) continue;

					partnerPos = partner.getPosition();
					partnerLook = partner.getLookVec();
					break;
				}
			}
		}

		// Take log
		String msg = elements.msgComponent.getText();
		int numeralId = ChatAnnotator.CHAT_ID_MANAGER_SERVER.getIdOnServerChat(msg, player);
		ChatAnnotator.CHAT_RECORDER.recordChatStatus(
				player,
				numeralId,
				msg,
				playerPos, playerLook,
				partnerPos, partnerLook
		);

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
		TextComponentString msgComponent = TextComponentUtils.findMsgComponent(args);
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
	 * Set received TextComponent to the proper style
	 * @param event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onReceivedClientChat(ClientChatReceivedEvent event)
	{
		if(!(event.getMessage() instanceof TextComponentTranslation)) return;
		EntityPlayer player = Minecraft.getMinecraft().player;

		ITextComponent iComponent = event.getMessage();
		TextComponentAnnotation msgComponent = TextComponentUtils.getComponentAnnotation(iComponent);

		// Already replaced or non-chat message. Do nothing
		if (Objects.isNull(msgComponent)) return;

		// Set chat to proper style based on the sender/receiver
		UUID receiverId = player.getUniqueID();
		msgComponent.toProperStyle(receiverId);

		TextComponentTranslation component = (TextComponentTranslation) event.getMessage();
		Object[] args = component.getFormatArgs();
		args[1] = msgComponent;
		event.setMessage(new TextComponentTranslation(component.getKey(), args));
	}

	/** =================
	 *    UTILS
	 * ================== */

	/**
	 * Create new componentTranslation with annotated message
	 */
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ComponentElements elements, UUID senderId, int dimension, int numeralId)
	{
		ITextComponent newMsgComponent = TextComponentUtils.createAnnotatedChat(elements.msgComponent, senderId, dimension, numeralId);
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
