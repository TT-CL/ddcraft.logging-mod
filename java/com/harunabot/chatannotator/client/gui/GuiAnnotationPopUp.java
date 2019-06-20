package com.harunabot.chatannotator.client.gui;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.util.text.StringTools;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

/**
 * Additional Gui for GuiChatAnnotation
 */
public class GuiAnnotationPopUp extends Gui
{
    /** Reference to the Minecraft object. */
    public Minecraft mc;
    /** Owner of this gui*/
    private GuiChatWithAnnotation parent;

	/** list of dialogue acts.*/
	private final List<DialogueAct> dialogueActs;
	/** component you want to annotate*/
	protected final TextComponentAnnotation component;
    /** A list of all the buttons in this container. */
    protected List<GuiButton> buttonList = Lists.<GuiButton>newArrayList();
    /** The button that was just pressed. */
    protected GuiButton selectedButton;

    /** position of component(got by org.lwjgl.input.Mouse.getX() or get y()) */
    protected final int componentX;
    protected final int componentY;

	private int buttonY;
	private final int BUTTON_FIRST_X = 5;
	private final int BUTTON_MARGIN = 3;
	private static final int BUTTON_WIDTH = GuiAnnotationButton.WIDTH;
	private static final int BUTTON_HEIGHT = GuiAnnotationButton.HEIGHT;
	private static final int BUTTON_FIRST_ID = 100;

	public GuiAnnotationPopUp(Minecraft mc, GuiChatWithAnnotation parent, int y, TextComponentAnnotation component, int mouseX, int mouseY)
	{
		super();

		this.mc = mc;
		this.parent = parent;
		this.buttonY = y - BUTTON_HEIGHT - 3*BUTTON_MARGIN;
		this.dialogueActs = DialogueAct.getList();
		this.component = component;
		this.componentX = mouseX;
		this.componentY = mouseY;
		setupButtons();
	}

    /**
     * Draws the popup.
     */
    public void drawPopup(int mouseX, int mouseY, float partialTicks)
    {
    	drawRect(BUTTON_FIRST_X - BUTTON_MARGIN,
    			buttonY - BUTTON_MARGIN,
    			BUTTON_FIRST_X + dialogueActs.size() * (BUTTON_WIDTH + BUTTON_MARGIN),
    			buttonY + BUTTON_HEIGHT + BUTTON_MARGIN,
    			0x7d333333
    			);

        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }

	private void setupButtons()
	{
		int buttonX = BUTTON_FIRST_X;
		for(DialogueAct action: dialogueActs)
		{
			this.buttonList.add(
				new GuiAnnotationButton(BUTTON_FIRST_ID + action.getId(), buttonX, buttonY, action)
			);
			buttonX += GuiAnnotationButton.WIDTH + BUTTON_MARGIN;
		}
	}

	public List<GuiButton> getButtons()
	{
		return this.buttonList;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            GuiButton guibutton = this.buttonList.get(i);

            if (guibutton.mousePressed(this.mc, mouseX, mouseY))
            {
                net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(parent, guibutton, this.buttonList);
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                    break;
                guibutton = event.getButton();
                this.selectedButton = guibutton;
                guibutton.playPressSound(this.mc.getSoundHandler());
                this.parent.actionPerformed(guibutton);
                if (this.parent.equals(this.mc.currentScreen))
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(parent, event.getButton(), this.buttonList));

                return;
            }
        }
	}

	protected void annotateComponent(DialogueAct dialogueAct)
	{
		// TODO: 別の形で送る
		String msg = "[" + dialogueAct.getName() + "]" + component.toIdenticalString();
		msg = StringTools.deleteIllegalCharacters(msg);

		component.annotateByReceiver(dialogueAct);
		component.toDefaultStyle();

		parent.sendChatMessage(msg, false);

        if(this.mc.ingameGUI.getChatGUI() instanceof MyGuiNewChat)
        {
        	MyGuiNewChat guiNewChat = (MyGuiNewChat)this.mc.ingameGUI.getChatGUI();
        	guiNewChat.replaceChatComponent(componentX, componentY, this.component);
        }

		ChatAnnotator.LOGGER.log(Level.INFO, "Annotated chat: [annotation]" + dialogueAct.getName() + ", [chat]" + component.getText());
	}

}
