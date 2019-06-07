package com.harunabot.chatannotator.util.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.harunabot.chatannotator.client.gui.DialogueAct;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TextComponentAnnotation extends TextComponentString
{
	/**
	 * dialogue act selected by text sender
	 */
	private final DialogueAct senderAnnotation;
	private DialogueAct receiverAnnotation;
	private final UUID senderId;
	private final String time;

	// TODO: sendername
    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, UUID senderId)
    {
    	super(msg);

    	this.senderAnnotation = senderAnnotation;
    	this.senderId = senderId;

    	this.time = new SimpleDateFormat("HH:mm:ss").format(new Date());
    	this.receiverAnnotation = null;
    }

    public String getTime()
    {
    	return this.time;
    }

    @Override
    public String getUnformattedComponentText()
    {
    	return this.getFormattedText();
    }

    public boolean isAnnotated()
    {
    	return receiverAnnotation != null;
    }

    public void toProperStyle(UUID receiverId)
    {
    	System.out.println(senderId + ", " + receiverId);
    	// Do nothing for sender
    	if(receiverId.equals(senderId)) {
    		this.setStyle(new Style().setColor(TextFormatting.YELLOW));
    		return;
    	}

    	this.setStyle(new Style().setColor(TextFormatting.BLUE));
    }

    @Override
    public String toString()
    {
    	return "AnnotatedTextComponent{text='" + this.getText() + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() +
    			", senderAnnotation=" + this.senderAnnotation + ", receiverAnnotation=" + receiverAnnotation + ", sender=" + this.senderId + ", time=" + this.time + '}';
    }

    /**
     * Get the component in format of log output.
     * @return
     */
    public String toLogString()
    {
    	return "{text:'" + this.getText() + "', senderId:" + this.senderId + ", senderAnnotation=" + this.senderAnnotation + ","
    			+ " receiverAnnotation=" + receiverAnnotation + ", time=" + this.time + '}';
    }
}
