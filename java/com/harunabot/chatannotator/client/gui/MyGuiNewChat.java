package com.harunabot.chatannotator.client.gui;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
    }

    public void setChatComponent(int mouseX, int mouseY, ITextComponent textcomponent)
    {
        if (!this.getChatOpen()) return;

    	try {
			// Reflect GuiNewChat private fields
	    	Field scrollPosField = GuiNewChat.class.getDeclaredField("scrollPos");
			scrollPosField.setAccessible(true);
		    int scrollPos = (int) scrollPosField.get(this);
		    // Local fields
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 40;
            j = MathHelper.floor((float)j / f);
            k = MathHelper.floor((float)k / f);

            // Find chat component from mouse position(fixed GuiChat.getChatComponent)
            if (!(j >= 0 && k >= 0)) return;

            int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (!(j <= MathHelper.floor((float)this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l)) return;

            int i1 = k / this.mc.fontRenderer.FONT_HEIGHT + scrollPos;
            if (!(i1 >= 0 && i1 < this.drawnChatLines.size())) return;

            ChatLine chatline = this.drawnChatLines.get(i1);
            int j1 = 0;

            for (ITextComponent itextcomponent : chatline.getChatComponent())
            {
            	System.out.println(itextcomponent.toString());
                if (itextcomponent instanceof TextComponentString)
                {
                    j1 += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)itextcomponent).getText(), false));

                    if (j1 > j)
                    {
                        //set
                    	return;
                    }
                }
            }
        }
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			System.err.println("Failed to create MyGuiNewChat!");
			e.printStackTrace();
		}
    }
}
