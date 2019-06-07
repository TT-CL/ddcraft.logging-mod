package com.harunabot.chatannotator.util.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.harunabot.chatannotator.client.gui.DialogueAct;

import net.minecraft.util.text.ITextComponent;
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

	private final int PARAM_NUM = 4;

	// TODO: sendername
    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, UUID senderId)
    {
    	this(msg,senderAnnotation, null, senderId, new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }

    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, DialogueAct receiverAnnotation, UUID senderId, String time)
    {
    	super(msg);

    	this.senderAnnotation = senderAnnotation;
    	this.receiverAnnotation = receiverAnnotation;
    	this.senderId = senderId;
    	this.time = time;
    }

    // Recreate from TextComponentString
    /*
    public TextComponentAnnotation(TextComponentString component)
    {
    	if(component.getSiblings().size() < PARAM_NUM)
    	{
    		Main.LOGGER.log(Level.ERROR, "TextComponentAnnotation: invalid componentString");

    	}
    	for(int i = 0; i < 4; i++)
    	{

    	}
    }
    */

    public TextComponentString toComponentString()
    {
    	if(!this.siblings.isEmpty())
    	{
    		this.siblings = Lists.<ITextComponent>newArrayList();
    	}
    	appendText((senderAnnotation != null) ? senderAnnotation.getName() : "null");
    	appendText((receiverAnnotation != null) ? receiverAnnotation.getName() : "null");
    	appendText(senderId.toString());
    	appendText(time.toString());

    	return (TextComponentString)this;
    }

    public String getTime()
    {
    	return this.time;
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
	public TextComponentAnnotation createCopy()
	{
		TextComponentAnnotation textcomponentannotation = new TextComponentAnnotation(this.getText(), this.senderAnnotation, this.receiverAnnotation, this.senderId, this.time);
		textcomponentannotation.setStyle(this.getStyle().createShallowCopy());

		for(ITextComponent iTextComponent : this.getSiblings())
		{
			textcomponentannotation.appendSibling(iTextComponent.createCopy());
		}

		return textcomponentannotation;
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
