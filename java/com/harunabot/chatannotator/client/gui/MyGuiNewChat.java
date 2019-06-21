package com.harunabot.chatannotator.client.gui;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
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
    // GuiNewChat private fields------
    /** Chat lines to be displayed in the chat box */
    private List<ChatLine> chatLines = null;
    /** List of the ChatLines currently drawn */
    private List<ChatLine> drawnChatLines = null;

    // field index for Reflections
    protected static final int CHATLINES_FIELD_INDEX = 3;
    protected static final int DRAWNCHATLINES_FIELD_INDEX = 4;
    protected static final int SCROLLPOS_FIELD_INDEX = 5;
    protected static final int ISSCROLLED_FIELD_INDEX = 6;

	public MyGuiNewChat(Minecraft mcIn)
	{
		super(mcIn);
		this.mc = mcIn;

		// Reflect GuiNewChat private fields---------------------------
		try {
			this.chatLines = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, this, CHATLINES_FIELD_INDEX);
			this.drawnChatLines = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, this, DRAWNCHATLINES_FIELD_INDEX);
		}
		catch(UnableToFindFieldException | UnableToAccessFieldException e)
		{
			LOGGER.log(Level.ERROR, "Reflection Error: Failed to create MyGuiNewChat!");
			e.printStackTrace();
		}
	}

	// Override to use overridden setChatLine()
	@Override
	/**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId)
    {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

	// Override to use overridden setChatLine()
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
    		int scrollPos = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, this, SCROLLPOS_FIELD_INDEX);

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
	            	ObfuscationReflectionHelper.setPrivateValue(GuiNewChat.class, this, true, ISSCROLLED_FIELD_INDEX); //this.isScrolled = true;
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
		catch(UnableToFindFieldException | UnableToAccessFieldException e)
		{
			LOGGER.log(Level.ERROR, "Reflection Error: Failed to execute MyGuiNewChat.setChatLine!");
			e.printStackTrace();
		}
    }

    public void replaceChatComponent(int mouseX, int mouseY, ITextComponent textcomponent)
    {
        if (!this.getChatOpen()) return;

    	try {
			// Reflect GuiNewChat private fields
    		int scrollPos = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, this, SCROLLPOS_FIELD_INDEX);

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

            _replaceChatComponent(chatline.getChatLineID(), chatline.getUpdatedCounter(), textcomponent);
        }
		catch(UnableToFindFieldException | UnableToAccessFieldException e)
		{
			LOGGER.log(Level.ERROR, "Reflection Error: Failed to replace chat component");
			e.printStackTrace();
		}
    }

    private void _replaceChatComponent(int id, int counter, ITextComponent component)
    {
    	for (int i = this.chatLines.size() - 1; i >= 0; --i)
		{
		    ChatLine chatline = this.chatLines.get(i);
		    if(chatline.getChatLineID() == id & chatline.getUpdatedCounter() == counter)
		    {
		    	this.chatLines.remove(i);
		    	this.chatLines.add(i, new ChatLine(chatline.getUpdatedCounter(), replaceAnnotationComponent(chatline.getChatComponent(), component), chatline.getChatLineID()));
		    	refreshChat();
		    	return;
		    }
		}
    }

    private ITextComponent replaceAnnotationComponent(ITextComponent oldComponent, ITextComponent component)
    {
    	if (! (oldComponent instanceof TextComponentTranslation))
    	{
    		LOGGER.log(Level.ERROR, "Failed to replace chatcomponent. Tried to replace non-TranslatableComponent:" + oldComponent.toString());
    		return oldComponent;
    	}

    	TextComponentTranslation textComponentTranslation  = (TextComponentTranslation) oldComponent;
    	Object[] args = textComponentTranslation.getFormatArgs();
    	for (int i = 0; i<args.length; i++)
    	{
    		if(args[i] instanceof TextComponentAnnotation)
    		{
    			args[i] = component;
    			break;
    		}
    	}

    	return new TextComponentTranslation(textComponentTranslation.getKey(), args);
    }

}
