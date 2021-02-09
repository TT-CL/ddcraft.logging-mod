package jp.ac.titech.c.cl.chatannotator.common.config;

import jp.ac.titech.c.cl.chatannotator.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;

@Config(modid = Reference.MOD_ID, type = Type.INSTANCE, name = Reference.MOD_ID)
public class ModConfig
{
	@Comment("Toggles whether to do label annotation or not. Disabled in serverOnlyMode")
	public static boolean enableAnnotationLabel = true;

	@Comment("(Serverside only) If true, clients can connect the server without the mod")
	public static boolean serverOnlyMode = false;
}