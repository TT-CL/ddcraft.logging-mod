package com.harunabot.chatannotator.client.gui;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

import com.harunabot.chatannotator.screenshot.ScreenRecorder;
import com.harunabot.chatannotator.util.text.StringTools;
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
	/** Annotation popup*/
	protected GuiAnnotationPopUp annotationPopUp;

	/** list of dialogue acts.*/
	private final List<DialogueAct> dialogueActs;
	/** text that appears when you open the input box(for re-inputting */
	protected static String inputFieldText = "";

	/** position for Annotatin Buttons */
	private static final int BUTTON_FIRST_X = 5;
	private static final int BUTTON_MARGIN = 3;
	private int buttonY;

	/** Position where last clicked */
	private int lastClickedX;
	private int lastClickedY;


    public GuiChatWithAnnotation()
	{
    	super();
    	dialogueActs = DialogueAct.getList();
	}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	if(this.annotationPopUp != null)
    	{
    		this.annotationPopUp.drawPopup(mouseX, mouseY, partialTicks);
    	}
    }

    @Override
    public void initGui()
    {
    	super.initGui();

    	this.buttonY = this.height - 20 - GuiAnnotationButton.HEIGHT;
    	this.annotationPopUp = null;
    	this.activatedButton = null;
    	this.inputField.setText(this.inputFieldText);
    	addAnnotationButtons();
    }

    public void addAnnotationButtons()
    {
    	int buttonX = BUTTON_FIRST_X;
    	for(DialogueAct action: dialogueActs)
    	{
    		this.buttonList.add(
    			new GuiAnnotationButton(action.getId(), buttonX, this.buttonY, action)
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
    	// 受け手を変えないといけないので大変
    	// NetworkManagerとか
    	msg = "<" + activatedButton.displayString + ">" + msg;
    	super.sendChatMessage(msg);

    	// send screenshot together
    	ScreenRecorder.reserveScreenshot();
    }

    // @Override
    public void sendChatMessage(String msg, boolean addToChat)
    {
        msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
        if (msg.isEmpty()) return;
        if (addToChat)
        {
        	// Add message with no annotation
        	String mainMsg = StringTools.separatePrefixBySymbols(msg, '<', '>').getRight();
        	mainMsg = (mainMsg != "") ? mainMsg : msg;
            this.mc.ingameGUI.getChatGUI().addToSentMessages(mainMsg);
        }
        if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.player, msg) != 0) return;

        this.mc.player.sendChatMessage(msg);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.lastClickedX = mouseX;
        this.lastClickedY = mouseY;

    	if(mouseButton == 0)
    	{
        	MyGuiNewChat guiNewChat = (MyGuiNewChat) this.mc.ingameGUI.getChatGUI();

    		if(Objects.nonNull(this.annotationPopUp))
    		{
    			this.annotationPopUp.mouseClicked(mouseX, mouseY, mouseButton);
    			this.annotationPopUp = null;
    		}

        	if(Objects.isNull(guiNewChat))
        	{
        		super.mouseClicked(mouseX, mouseY, mouseButton);
        		return;
        	}

            ITextComponent itextcomponent = guiNewChat.getChatComponent(Mouse.getX(), Mouse.getY());
            if (itextcomponent != null && this.handleComponentClick(itextcomponent))
            {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

	@Override
	public boolean handleComponentClick(ITextComponent component)
	{
		// Default action for non-annotationComponent
		if(!(component instanceof TextComponentAnnotation))
		{
			// TODO: stop click event in smarter way
			// stop default click event - annoys annotators
			return false;
			//return super.handleComponentClick(component);
		}

        ClickEvent clickevent = component.getStyle().getClickEvent();
        if(clickevent == null || !(clickevent instanceof AnnotationClickEvent))
    	{
        	return false;
        }
        // Note: Don't need AnnotationClickEvent instance; it's just for checking

        // Show GUI
        MyGuiNewChat guiNewChat = (MyGuiNewChat) this.mc.ingameGUI.getChatGUI();
        int chatlineNumber = guiNewChat.getChatLineNumber(Mouse.getX(), Mouse.getY());
        if (chatlineNumber < 0) return false;
        this.annotationPopUp = new GuiAnnotationPopUp(mc, this, this.lastClickedX, this.lastClickedY, (TextComponentAnnotation) component, Mouse.getX(), Mouse.getY(), chatlineNumber);

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

	// For CocoaInput
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    	super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
    	super.actionPerformed(button);
    	if (!(button instanceof GuiAnnotationButton)) return;

    	// Annotate received chat
    	if(this.annotationPopUp != null)
    	{
    		for(GuiButton popupButton : this.annotationPopUp.getButtons())
    		{
    			if(button.equals(popupButton))
    			{
    				this.annotationPopUp.annotateComponent(((GuiAnnotationButton)button).dialogueAct);
    				this.annotationPopUp = null;
    				return;
    			}
    		}
    	}

    	// Default button -> change activated button
    	changeActivatedButton((GuiAnnotationButton)button);
    }

    protected void changeActivatedButton(GuiAnnotationButton button) throws IOException
    {
    	if(activatedButton != null)
    	{
    		activatedButton.deactivate();
    	}
    	button.activate();
    	activatedButton = button;
    }
}
