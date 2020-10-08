package jp.ac.titech.c.cl.chatannotator.util;

public class Reference {
	public static final String MOD_ID = "chatannotator";
	public static final String NAME = "Chat Annotator Mod";
	private static final String MAJOR     = "@MAJOR@";
	private static final String MINOR     = "@MINOR@";
	private static final String MCVERSION = "@MC_VERSION@";
	public static final String VERSION = MCVERSION + "-" + MAJOR + "." + MINOR;

	public static final String CLIENT_PROXY_CLASS ="com.harunabot.chatannotator.proxy.ClientProxy";
	public static final String SERVER_PROXY_CLASS ="com.harunabot.chatannotator.proxy.ServerProxy";
	public static final String COMMON_PROXY_CLASS ="com.harunabot.chatannotator.proxy.CommonProxy";

	public static final int GUI_CHAT_ANNOTATOR = 0;
}
