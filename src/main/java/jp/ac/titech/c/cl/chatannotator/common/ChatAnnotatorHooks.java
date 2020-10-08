package jp.ac.titech.c.cl.chatannotator.common;

import javax.annotation.Nullable;

import jp.ac.titech.c.cl.chatannotator.event.AnnotationEvent;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class ChatAnnotatorHooks
{
    @Nullable
    public static TextComponentAnnotation onAnnotationEvent(TextComponentAnnotation comp, EntityPlayerMP player)
    {
    	AnnotationEvent event = new AnnotationEvent(comp, player);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return comp;
    }
}
