package com.harunabot.chatannotator.util.handlers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.harunabot.chatannotator.Main;
import com.harunabot.chatannotator.client.gui.DialogueAct;
import com.harunabot.chatannotator.server.AnnotationLog;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class AnnotationHandler
{
	private static final String DIR_NAME = "annotationLogs";

	public static AnnotationLog annotationLog;

	public static void preInit(FMLPreInitializationEvent event)
	{
		// no pre-initialization for remote client
		if (Minecraft.getMinecraft().world.isRemote) return;

		// make output file if on server
		// TODO: separate by world
		String date = new SimpleDateFormat("yy-MM-dd_HH.mm.ss").format(new Date());
		String filePath = DIR_NAME + "/chatLog_" + date + ".log";

		createOutputFile(DIR_NAME, filePath);

		// initialize logger
		annotationLog = new AnnotationLog(filePath);
	}

	public static void init(FMLInitializationEvent event)
	{

	}

	public static void postInit(FMLPostInitializationEvent event)
	{
	}


	@SubscribeEvent
	public static void onServerChat(ServerChatEvent event)
	{
		/**
		 * change text component(message) in translatable component to annotated component
		 */
		ITextComponent component = event.getComponent();
		if(!(event.getComponent() instanceof TextComponentTranslation)) return;

		ITextComponent newComponent = createAnnotatedServerChat((TextComponentTranslation) component, event);

		event.setComponent(newComponent);
		// event.setComponent(event.getComponent().setStyle(style));
		//annotationLog.writeLog();
		// いい感じに保存する
		/*
		Style style = new Style();
		style.setColor(TextFormatting.AQUA);
		event.setComponent(event.getComponent().setStyle(style));

		System.out.println(event.getMessage());]
		*/

		// take log
		if( !event.getPlayer().world.isRemote)
		{
			annotationLog.writeLog();
		}
	}
	private static ITextComponent createAnnotatedServerChat(TextComponentTranslation component, ServerChatEvent event)
	{
		Object[] args = component.getFormatArgs();

		Object msgObject = args[1];
		if(!(msgObject instanceof TextComponentString))
		{
			// ERROR if second component is not text
			Main.LOGGER.log(Level.ERROR, "invalid translatable component: " + component.getFormattedText());
			return component;
		}

		TextComponentString msgComponent = (TextComponentString) msgObject;
		String rawMsg = msgComponent.getText();

		// main message
		// TODO: error check
		String msg = rawMsg.substring(rawMsg.indexOf(">") + 1);

		// dialogue act selected by sender
		String annotationStr = rawMsg.substring(1,rawMsg.indexOf(">"));
		DialogueAct annotation = DialogueAct.convertFromName(annotationStr);
		if(annotation == null) {
			// ERROR if no annotation is found
			Main.LOGGER.log(Level.ERROR, "invalid textcomponent: {msg: " + msg + ", annotation: " + annotationStr + "}");
			return component;
		}

		// sender's UUID
		UUID senderId = event.getPlayer().getUniqueID();

		ITextComponent newMsgComponent = new TextComponentAnnotation(msg, annotation, senderId);
		args[1] = newMsgComponent;

		return new TextComponentTranslation(component.getKey(), args);
	}


	@SubscribeEvent
	public static void onReceivedChat(ClientChatReceivedEvent event)
	{
		if(event.getType() != ChatType.CHAT) return;

		Style style = new Style();
		style.setColor(TextFormatting.YELLOW);
		style.setColor(TextFormatting.UNDERLINE);
		//style.setHoverEvent()

		//event.setMessage(event.getMessage().setStyle(style));
	}

	public static void onAnnotatedChat()
	{

	}

	/**
	 * Create output file for log
	 */
	protected static void createOutputFile(String directoryName, String filePath)
	{
		File dir = new File(directoryName);
        File file = new File(filePath);
        try {
        	if(!dir.exists()) dir.mkdir();
            file.createNewFile();
            Main.LOGGER.log(Level.INFO, "Successfully created output file");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
