package jp.ac.titech.c.cl.chatannotator.proxy;


import org.apache.logging.log4j.Level;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.annotator.server.AnnotationRecorder;
import jp.ac.titech.c.cl.chatannotator.logger.server.ChatRecorder;
import jp.ac.titech.c.cl.chatannotator.screenshot.ScreenRecorder;
import jp.ac.titech.c.cl.chatannotator.server.ChatIdManagerServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Load resources and do some stuffs on the server side
 */
@Mod.EventBusSubscriber(Side.SERVER)
public class ServerProxy extends CommonProxy
{
	@Override
    public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
    }

	@Override
    public void init(FMLInitializationEvent event) {
		super.init(event);

		try
		{
			ChatAnnotator.modDirectory.mkdir();
		}
		catch (SecurityException e)
		{
			ChatAnnotator.LOGGER.log(Level.ERROR, "Failed to create a directory");
		}

		ChatAnnotator.CHAT_RECORDER = new ChatRecorder();
		ChatAnnotator.ANNOTATION_RECORDER = new AnnotationRecorder();
		ChatAnnotator.CHAT_ID_MANAGER_SERVER = new ChatIdManagerServer();
    }

	@Override
    public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
    }
}