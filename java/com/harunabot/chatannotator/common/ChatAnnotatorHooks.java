package com.harunabot.chatannotator.common;

import javax.annotation.Nullable;

import com.harunabot.chatannotator.event.AnnotationEvent;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraftforge.common.MinecraftForge;

public class ChatAnnotatorHooks
{
    @Nullable
    public static TextComponentAnnotation onAnnotationEvent(TextComponentAnnotation comp)
    {
    	AnnotationEvent event = new AnnotationEvent(comp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return comp;
    }
}
