package com.harunabot.chatannotator.util.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.util.text.event.AnnotationClickEvent;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TextComponentAnnotation extends TextComponentString
{
	/**
	 * dialogue act selected by text sender
	 */
	protected DialogueAct senderAnnotation;
	protected DialogueAct receiverAnnotation;
	protected String senderId;
	protected String time;
	protected String fullMsg;

	private final int PARAM_NUM = 5;

	// FIXME: should contain sendername?
    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, UUID senderId)
    {
    	this(msg,senderAnnotation, null, senderId.toString(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }

    public TextComponentAnnotation(String msg, DialogueAct senderAnnotation, DialogueAct receiverAnnotation, String senderId, String time)
    {
    	super(msg);

    	this.senderAnnotation = senderAnnotation;
    	this.receiverAnnotation = receiverAnnotation;
    	this.senderId = senderId.toString();
    	this.time = time;
    	this.fullMsg = msg;
    }

    // Recreate from TextComponentString
    public TextComponentAnnotation(TextComponentString component)
    {
    	super(component.getText());

    	try
    	{
	    	List<ITextComponent> siblings = component.getSiblings();
	    	this.senderAnnotation = DialogueAct.convertFromName(siblings.get(0).getUnformattedText());
	    	this.receiverAnnotation = DialogueAct.convertFromName(siblings.get(1).getUnformattedText());
	    	this.senderId = siblings.get(2).getUnformattedText();
	    	this.time = siblings.get(3).getUnformattedText();
	    	this.fullMsg = siblings.get(4).getUnformattedText();
	    	this.setStyle(component.getStyle().createDeepCopy());
    	}
    	catch (Exception e)
    	{
    		System.out.println(e);
    		ChatAnnotator.LOGGER.log(Level.ERROR, "TextComponentAnnotation::invalid componentString : " + component);
    		this.senderAnnotation = null;
    		this.receiverAnnotation = null;
    		this.senderId = "";
    		this.time = "";
    		this.fullMsg = "";

    		return;
    	}
    }

    public void annotateByReceiver(DialogueAct annotation)
    {
    	this.receiverAnnotation = annotation;
    }

    public TextComponentString toComponentString()
    {
    	if(!this.siblings.isEmpty())
    	{
    		this.siblings = Lists.<ITextComponent>newArrayList();
    	}
    	TextComponentString componentString = (TextComponentString) this.createCopy();
    	componentString.appendText((senderAnnotation != null) ? senderAnnotation.getName() : "null");
    	componentString.appendText((receiverAnnotation != null) ? receiverAnnotation.getName() : "null");
    	componentString.appendText(senderId.toString());
    	componentString.appendText(time.toString());
    	componentString.appendText(fullMsg);

    	// Set style to prevent it from changing to String
		componentString.setStyle(new Style().setColor(TextFormatting.BLACK));

    	return componentString;
    }

    public String getTime()
    {
    	return this.time;
    }

    public DialogueAct getSenderAnnotation()
    {
    	return this.senderAnnotation;
    }

    public DialogueAct getReceiverAnnotation()
    {
    	return this.receiverAnnotation;
    }

    public boolean isAnnotated()
    {
    	return receiverAnnotation != null;
    }

    public void toProperStyle(UUID receiverId)
    {
    	// Sender -> default
    	if(receiverId.toString().equals(senderId)) {
    		this.toDefaultStyle();
    		return;
    	}

    	// Receiver -> AnnotationClickEvent, color = YELLOW, underlined
    	Style newStyle = this.getStyle();
    	newStyle.setColor(TextFormatting.YELLOW);
    	newStyle.setUnderlined(true);
    	newStyle.setClickEvent(new AnnotationClickEvent());
    	this.setStyle(newStyle);
    }

    public void changeColor(Boolean annotating)
    {
    	Style style = this.getStyle();
    	if(annotating)
    	{
    		style.setColor(TextFormatting.AQUA);
    	}
    	else
    	{
    		style.setColor(TextFormatting.YELLOW);
    	}
    }

    public void toDefaultStyle()
    {
    	this.setStyle(new Style());
    }

	@Override
	public TextComponentAnnotation createCopy()
	{
		return this.createPartialCopy(this.getText());
	}

    public TextComponentAnnotation createPartialCopy(String msg)
	{
    	msg = StringTools.deleteIllegalCharacters(msg);
    	if(!(this.fullMsg.contains(msg)) && !(this.fullMsg.equals(msg))) {
    		System.out.println("Not partial: '" + msg + "' , '" + this.fullMsg + "'");
    	}

    	TextComponentAnnotation textcomponentannotation = new TextComponentAnnotation(msg, this.senderAnnotation, this.receiverAnnotation, this.senderId.toString(), this.time);
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

    public String toIdenticalString()
    {
    	return "{text:'" + this.getText() + "', senderId:" + this.senderId + ", time=" + this.time + '}';
    }

}