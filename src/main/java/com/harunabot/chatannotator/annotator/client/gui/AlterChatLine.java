package com.harunabot.chatannotator.annotator.client.gui;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

public class AlterChatLine extends ChatLine
{
    /** int value to refer to existing Chat Line Groups; chats sent at the same time have same group ID */
    private final int chatLineGroupNumber;

    public AlterChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn, int chatLineGroupNumIn)
    {
    	super(updateCounterCreatedIn, lineStringIn, chatLineIDIn);
    	this.chatLineGroupNumber = chatLineGroupNumIn;
    }

    public int getChatLineNumber()
    {
    	return this.chatLineGroupNumber;
    }
}
