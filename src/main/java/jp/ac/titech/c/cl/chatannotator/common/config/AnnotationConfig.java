package jp.ac.titech.c.cl.chatannotator.common.config;

import jp.ac.titech.c.cl.chatannotator.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;

@Config(modid = Reference.MOD_ID, type = Type.INSTANCE, name = Reference.MOD_ID + ".annotation")
public class AnnotationConfig
{
	@Comment("Toggles whether to do label annotation or not")
	public static boolean enableAnnotationLabel = true;
}