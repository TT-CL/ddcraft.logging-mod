package com.harunabot.chatannotator.client.gui;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GuiNewChat extended for TextComponentAnnotation
 * !Uses reflection to access private fields!
 */
@SideOnly(Side.CLIENT)
public class MyGuiNewChat extends GuiNewChat
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    /**
     *  GuiNewChat private fields
     */
    /** Chat lines to be displayed in the chat box */
    private List<ChatLine> chatLines = null;
    /** List of the ChatLines currently drawn */
    private List<ChatLine> drawnChatLines = null;

	public MyGuiNewChat(Minecraft mcIn)
	{
		super(mcIn);
		this.mc = mcIn;

		// Reflect GuiNewChat private fields---------------------------
		try {
			Field chatLinesField = GuiNewChat.class.getDeclaredField("chatLines");
			Field drawnChatLinesField = GuiNewChat.class.getDeclaredField("drawnChatLines");
			chatLinesField.setAccessible(true);
			drawnChatLinesField.setAccessible(true);
			this.chatLines = (List<ChatLine>) chatLinesField.get(this);
			this.drawnChatLines = (List<ChatLine>) drawnChatLinesField.get(this);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			System.err.println("Failed to create MyGuiNewChat!");
			e.printStackTrace();
		}
	}


	@Override
	/**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId)
    {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

	@Override
    public void refreshChat()
    {
		this.drawnChatLines.clear();
		super.resetScroll();

		for (int i = chatLines.size() - 1; i >= 0; --i)
		{
		    ChatLine chatline = chatLines.get(i);
		    this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
		}
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly)
    {
    	try {
			// Reflect GuiNewChat private fields--------------------------------------------
	    	Field scrollPosField = GuiNewChat.class.getDeclaredField("scrollPos");
	    	Field isScrolledField = GuiNewChat.class.getDeclaredField("isScrolled");
			scrollPosField.setAccessible(true);
			isScrolledField.setAccessible(true);
		    int scrollPos = (int) scrollPosField.get(this);


			// GuiNewChat.setChatLine--------------------------------------------------------
	        if (chatLineId != 0)
	        {
	            this.deleteChatLine(chatLineId);
	        }

	        int i = MathHelper.floor((float)this.getChatWidth() / this.getChatScale());
	        List<ITextComponent> list = MyGuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRenderer, false, false);
	        boolean flag = this.getChatOpen();

	        for (ITextComponent itextcomponent : list)
	        {
	            if (flag && scrollPos > 0)
	            {
	                isScrolledField.set(this, true); //this.isScrolled = true;
	                this.scroll(1);
	            }

	            System.out.println(itextcomponent);
	            this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
	        }

	        while (drawnChatLines.size() > 100)
	        {
	        	this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
	        }

	        if (!displayOnly)
	        {
	            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

	            while (this.chatLines.size() > 100)
	            {
	            	this.chatLines.remove(this.chatLines.size() - 1);
	            }
	        }
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			System.err.println("Failed to execute MyGuiNewChat.setChatLine!");
			e.printStackTrace();
		}

		System.out.println("SET_CHAT_LINE");
    }
}
