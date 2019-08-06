package com.harunabot.chatannotator.client.gui;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

public class MyChatLine extends ChatLine
{
    /** int value to refer to existing Chat Line Groups; chats sent at the same time have same group ID */
    private final int chatLineGroupID;

    public MyChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn, int chatLineGroupIDIn)
    {
    	super(updateCounterCreatedIn, lineStringIn, chatLineIDIn);
    	this.chatLineGroupID = chatLineGroupIDIn;
    }

    public int getChatLineGroupID()
    {
    	return this.chatLineGroupID;
    }
}
