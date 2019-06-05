package com.harunabot.chatannotator.util.text;

import java.util.UUID;

import com.harunabot.chatannotator.client.gui.DialogueAct;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo.Color;

public class TextComponentAnnotation extends TextComponentString
{
	/**
	 * dialogue act selected by text sender
	 */
	private final DialogueAct senderAnnotation;
	private final UUID senderId;

    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, UUID senderId)
    {
    	super(msg);

    	this.senderAnnotation = senderAnnotation;
    	this.senderId = senderId;
    	this.setStyle(new Style().setColor(TextFormatting.BLUE));
    }

    @Override
    public String toString()
    {
    	return "AnnotatedTextComponent{text='" + this.getText() + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() +
    			", dialogueact=" + this.senderAnnotation + ", sender=" + this.senderId + '}';
    }
}
