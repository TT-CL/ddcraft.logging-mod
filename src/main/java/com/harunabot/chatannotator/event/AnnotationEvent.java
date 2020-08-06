package com.harunabot.chatannotator.event;

import com.harunabot.chatannotator.annotator.DialogueAct;
import com.harunabot.chatannotator.common.ChatAnnotatorHooks;
import com.harunabot.chatannotator.util.handlers.event.ChatEventHandler;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * AnnotationEvent is fired whenever a TextComponentAnnotation is annotated. <br>
 * TODO: should fix details below
 * This event is fired via {@link ChatAnnotatorHooks#onAnnotationEvent(TextComponentAnnotation)},
 * which is executed by the {@link ChatEventHandler#processChatMessage(CPacketChatMessage)}<br>
 * <br>
 * {@link #username} contains the username of the player sending the chat message.<br>
 * {@link #message} contains the message being sent.<br>
 * {@link #player} the instance of EntityPlayerMP for the player sending the chat message.<br>
 * {@link #component} contains the instance of ChatComponentTranslation for the sent message.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class AnnotationEvent extends Event
{
	private final TextComponentAnnotation component;
	private final EntityPlayerMP player;

    public AnnotationEvent(TextComponentAnnotation component, EntityPlayerMP player)
    {
        super();
        this.component = component;
        this.player = player;
    }

    public boolean isAnnotationMatched()
    {
    	return getSenderAnnotation() == getReceiverAnnotation();
    }

    public String getMessage()
    {
    	return this.component.getText();
    }

    public EntityPlayerMP getPlayer()
    {
    	return this.player;
    }

    public int getDimension()
    {
    	return this.component.getDimension();
    }

    public DialogueAct getSenderAnnotation()
    {
    	return this.component.getSenderAnnotation();
    }

    public DialogueAct getReceiverAnnotation()
    {
    	return this.component.getReceiverAnnotation();
    }
}
