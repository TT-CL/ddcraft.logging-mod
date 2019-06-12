package com.harunabot.chatannotator.client.gui;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.harunabot.chatannotator.util.text.TextComponentAnnotation;
import com.harunabot.chatannotator.util.text.event.AnnotationClickEvent;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class GuiChatWithAnnotation extends GuiChat
{
    private static final Logger LOGGER = LogManager.getLogger();

	/** Button that is now selecting */
	protected GuiAnnotationButton activatedButton;

	/** list of dialogue acts.*/
	private final List<DialogueAct> dialogueActs;
	/** text that appears when you open the input box(for re-inputting */
	protected static String inputFieldText = "";

	// TODO: 解像度に合わせてyをセットすべき
	private static final int BUTTON_FIRST_X = 5;
	private static final int BUTTON_Y = 205;
	private static final int BUTTON_MARGIN = 3;


    public GuiChatWithAnnotation()
	{
    	super();
    	dialogueActs = DialogueAct.getList();
	}

    @Override
    public void initGui()
    {
    	super.initGui();

    	addAnnotationButtons();
    	activatedButton = null;
    	this.inputField.setText(this.inputFieldText);
    }

    public void addAnnotationButtons()
    {
    	int buttonX = BUTTON_FIRST_X;
    	for(DialogueAct action: dialogueActs)
    	{
    		this.buttonList.add(
    			new GuiAnnotationButton(action.getId(), buttonX, BUTTON_Y, action)
			);
    		buttonX += GuiAnnotationButton.WIDTH + BUTTON_MARGIN;
    	}
    }

    @Override
    public void sendChatMessage(String msg)
    {
    	// pass commands
    	if(msg.startsWith("/"))
    	{
    		super.sendChatMessage(msg);
    		return;
    	}

    	// failure: dialogue act is not selected
    	if(activatedButton == null)
    	{
    		inputFieldText = msg;

    		// TODO: 管理者なら無視するようにする？
    		// show error message only to the sender(in a red text)
    		TextComponentString errorMessage = new TextComponentString("対話行為が選択されていません >> " + msg);
    		Style style = new Style();
    		style.setColor(TextFormatting.RED);
    		this.mc.ingameGUI.addChatMessage(ChatType.SYSTEM, errorMessage.setStyle(style));
    		return;
    	}

    	// success
    	inputFieldText = "";
    	// TODO: チャットの形式を変える
    	msg = "<" + activatedButton.displayString + ">" + msg;
    	super.sendChatMessage(msg);
    }

	@Override
	public boolean handleComponentClick(ITextComponent component)
	{
		// TextComponentにされてしまっているので受け手側を変える or  無理やりやる

		// Default action for non-annotationComponent
		if(!(component instanceof TextComponentAnnotation))
		{
	        System.out.println("COMPONENT ERROR");
	        System.out.println(component.toString());
			//return super.handleComponentClick(component);
		}

//		TextComponentString componentAnnotation = (TextComponentAnnotation) component;
//        ClickEvent clickevent = componentAnnotation.getStyle().getClickEvent();
        ClickEvent clickevent = component.getStyle().getClickEvent();
        if(clickevent == null || !(clickevent instanceof AnnotationClickEvent))
    	{
            System.out.println("TYPE ERROR");
        	//return false;
        }

        System.out.println("CLICKED");

        // Don't need AnnotationClickEvent instance; it's just for checking

        // Show GUI

        // send annotation to the server
        // this.sendChat("",false?);

        // Reset style
        //componentAnnotation.toDefaultStyle();
        component.setStyle(new Style());

        return true;
	}



/*=========================================================================================*/


	@Override
	protected void handleComponentHover(ITextComponent component, int x, int y)
	{
		// Default action for non-annotationComponent
		if(!(component instanceof TextComponentTranslation))
		{
			super.handleComponentHover(component, x, y);
			return;
		}


	}

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
    	super.actionPerformed(button);

    	if(button instanceof GuiAnnotationButton)
    	{
    		_actionPerformed((GuiAnnotationButton)button);
    	}
    }

    /**
     * change activated(selected) button
     */
    protected void _actionPerformed(GuiAnnotationButton button) throws IOException
    {
    	if(activatedButton != null)
    	{
    		activatedButton.deactivate();
    	}
    	button.activate();
    	activatedButton = button;
    }
}
