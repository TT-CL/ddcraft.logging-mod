package com.harunabot.chatannotator.util.text.event;

import net.minecraft.util.text.event.ClickEvent;

public class AnnotationClickEvent extends ClickEvent
{
    public AnnotationClickEvent()
	{
		super(ClickEvent.Action.RUN_COMMAND, "");
    }
}
