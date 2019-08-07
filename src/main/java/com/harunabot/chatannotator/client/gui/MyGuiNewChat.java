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
// TODO: should use something other than reflection to make this mod compatible with other mods
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

    // counter for chatline number
    protected int chatLineNumberCount = 0;

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

	            this.drawnChatLines.add(0, new MyChatLine(updateCounter, itextcomponent, chatLineId, chatLineNumberCount));
	        }

	        while (drawnChatLines.size() > 100)
	        {
	        	this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
	        }

	        if (!displayOnly)
	        {
	            this.chatLines.add(0, new MyChatLine(updateCounter, chatComponent, chatLineId, chatLineNumberCount));

	            while (this.chatLines.size() > 100)
	            {
	            	this.chatLines.remove(this.chatLines.size() - 1);
	            }
	        }

	        // Update chatgroup counter-------------------------------------------------------
	        this.chatLineNumberCount++;
		}
		catch(UnableToFindFieldException | UnableToAccessFieldException e)
		{
			LOGGER.log(Level.ERROR, "Reflection Error: Failed to execute MyGuiNewChat.setChatLine!");
			e.printStackTrace();
		}
    }

    public int getChatLineNumber(int mouseX, int mouseY)
    {
		if (!this.getChatOpen()) return -1;

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

			// Find chatline from mouse position(fixed GuiChat.getChatComponent)
			if (!(j >= 0 && k >= 0)) return -1;

			int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
			if (!(j <= MathHelper.floor((float)this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l)) return -1;
			int i1 = k / this.mc.fontRenderer.FONT_HEIGHT+ scrollPos;
			if (!(i1 >= 0 && i1 < this.drawnChatLines.size())) return -1;

	        ChatLine chatline = this.drawnChatLines.get(i1);

	        if(chatline instanceof MyChatLine)
	        {
	        	System.out.println(((MyChatLine)chatline).getChatLineNumber() + ": " + chatline.getChatComponent().toString());
	        	return ((MyChatLine)chatline).getChatLineNumber();
	        }


        }
        catch(UnableToFindFieldException | UnableToAccessFieldException e)
        {
                LOGGER.log(Level.ERROR, "Reflection Error: Failed to replace chat component");
                e.printStackTrace();
        }

        return -1;
    }

    public ITextComponent annotateChatComponent(int chatLineNum, DialogueAct annotation)
    {
        if (!this.getChatOpen()) return null;


        for (int i = this.chatLines.size() - 1; i >= 0; --i)
        {
        	MyChatLine chatLine = (MyChatLine) chatLines.get(i);
        	if(chatLine.getChatLineNumber() == chatLineNum)
        	{
        		ITextComponent component = _annotateChatComponent(chatLine.getChatComponent(), annotation);
        		this.chatLines.remove(i);
		    	this.chatLines.add(i, new MyChatLine(chatLine.getUpdatedCounter(), component, chatLine.getChatLineID(), chatLine.getChatLineNumber()));
		    	refreshChat();
        		return findComponentAnnotation(component);
        	}
        }

        return null;
    }

    private ITextComponent _annotateChatComponent(ITextComponent component, DialogueAct annotation)
    {
    	if (! (component instanceof TextComponentTranslation))
    	{
    		LOGGER.log(Level.ERROR, "Failed to replace chatcomponent. Tried to replace non-TranslatableComponent:" + component.toString());
    		return component;
    	}

    	TextComponentTranslation textComponent = (TextComponentTranslation) component;
    	Object[] args = textComponent.getFormatArgs();
    	for (int i = 0; i < args.length; i++)
    	{
    		if(args[i] instanceof TextComponentAnnotation)
    		{
    			TextComponentAnnotation componentAnnotation = (TextComponentAnnotation) args[i];
    			componentAnnotation.annotateByReceiver(annotation);
    			componentAnnotation.toDefaultStyle();
    			args[i]= componentAnnotation;
    		}
    	}

    	return component;
    }

    public ITextComponent changeChatComponentColor(int chatLineNum, boolean annotating)
    {
        if (!this.getChatOpen()) return null;

        for (int i = this.chatLines.size() - 1; i >= 0; --i)
        {
        	MyChatLine chatLine = (MyChatLine) chatLines.get(i);
        	if(chatLine.getChatLineNumber() == chatLineNum)
        	{
        		ITextComponent component = _changeChatComponentColor(chatLine.getChatComponent(), annotating);
        		this.chatLines.remove(i);
		    	this.chatLines.add(i, new MyChatLine(chatLine.getUpdatedCounter(), component, chatLine.getChatLineID(), chatLine.getChatLineNumber()));
		    	refreshChat();

        		return findComponentAnnotation(component);
        	}
        }

        return null;
    }

    protected ITextComponent _changeChatComponentColor(ITextComponent component, boolean annotating)
    {
    	if (!(component instanceof TextComponentTranslation))
    	{
    		LOGGER.log(Level.ERROR, "Failed to replace chatcomponent. Tried to replace non-TranslatableComponent:" + component.toString());
    		return component;
    	}

    	TextComponentTranslation textComponent = (TextComponentTranslation) component;
    	Object[] args = textComponent.getFormatArgs();
    	for (int i = 0; i < args.length; i++)
    	{
    		if(args[i] instanceof TextComponentAnnotation)
    		{
    			TextComponentAnnotation componentAnnotation = (TextComponentAnnotation) args[i];
    			componentAnnotation.changeColor(annotating);
    			args[i] = componentAnnotation;
    		}
    	}

    	return component;
    }

    /**
     * Private method to return textcomponentAnnotation in annotateChatComponent and changeChatComponentColor
     * @param component
     * @return
     */
    private static ITextComponent findComponentAnnotation(ITextComponent component)
    {
    	if (!(component instanceof TextComponentTranslation)) return component;

    	TextComponentTranslation textComponent = (TextComponentTranslation) component;
    	Object[] args = textComponent.getFormatArgs();
    	for (int i = 0; i < args.length; i++)
    	{
    		if(args[i] instanceof TextComponentAnnotation)
    		{
    			return (TextComponentAnnotation)args[i];
    		}
    	}
    	return component;
    }

}
