package jp.ac.titech.c.cl.chatannotator.common.config;

import jp.ac.titech.c.cl.chatannotator.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;

@Config(modid = Reference.MOD_ID, type = Type.INSTANCE, name = Reference.MOD_ID)
public class ModConfig
{
	@Comment("Serverside-only mode. If true, clients can connect the server without the mod.")
	public static boolean serverOnlyMode = false;

	@Comment({"Configs valid only if client-side is also modded.", "Disabled if serverOnlyMode is true."})
	public static ClientOption clientOption = new ClientOption();


	public static class ClientOption
	{
		@Comment("Toggles whether to do label annotation or not.")
		public boolean enableAnnotationLabel = false;
	}

	public static boolean isAnnotationEnabled()
	{
		return !serverOnlyMode && clientOption.enableAnnotationLabel;
	}
}