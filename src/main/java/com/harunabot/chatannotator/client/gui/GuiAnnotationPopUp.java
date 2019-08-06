package com.harunabot.chatannotator.client.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** base position(clicked position) */
    private int baseX;
	private int baseY;

	/** base position for buttons */
	private int buttonX;
	private int buttonY;

	/** position of popup rects*/
	protected Map<String, Integer> mainPos;
	protected Map<String, Integer> middlePos;
	protected Map<String, Integer> edgePos;

	private static final int BUTTON_MARGIN = 3;
	private static final int MIN_BUTTON_LEFT = 5;
	private static final int BUTTON_WIDTH = GuiAnnotationButton.WIDTH;
	private static final int BUTTON_HEIGHT = GuiAnnotationButton.HEIGHT;
	private static final int BUTTON_FIRST_ID = 100;

	private static final int BACKGROUND_COLOR = 0xeeffffff; //white, opacity = 100
	private static final int EDGERECT_WIDTH = 10;
	private static final int EDGERECT_HEIGHT = 6;


	public GuiAnnotationPopUp(Minecraft mc, GuiChatWithAnnotation parent, int clickedX, int clickedY, TextComponentAnnotation component, int mouseX, int mouseY)
	{
		super();

		this.mc = mc;
		this.parent = parent;

		this.baseX = clickedX;
		this.baseY = clickedY - (/*2 */ BUTTON_MARGIN);

		this.dialogueActs = DialogueAct.getList();
		this.component = component;
		this.componentX = mouseX;
		this.componentY = mouseY;

		// init buttons
		setupButtons(this.baseX, this.baseY);
		// init rectangles
		setupRects(this.baseX, this.baseY, this.buttonX, this.buttonY);

		changeComponentColor(true);
	}

	private void setupButtons(int baseX, int baseY)
	{
		int length = (BUTTON_WIDTH + BUTTON_MARGIN) * dialogueActs.size() - BUTTON_MARGIN;
		int minRight = baseX + EDGERECT_WIDTH;

		this.buttonY = baseY - (EDGERECT_HEIGHT * 2) - (BUTTON_MARGIN * 3) - BUTTON_HEIGHT;
		this.buttonX = (baseX - length/2 < MIN_BUTTON_LEFT) ? MIN_BUTTON_LEFT : baseX - length/2;
		buttonX = (buttonX + length/2 < minRight) ? minRight - length/2 : buttonX;

		int x = buttonX;

		for(DialogueAct action: dialogueActs)
		{
			this.buttonList.add(
				new GuiAnnotationButton(BUTTON_FIRST_ID + action.getId(), x, buttonY, action)
			);
			x += BUTTON_WIDTH + BUTTON_MARGIN;
		}
	}

	protected void setupRects(int baseX, int baseY, int buttonX, int buttonY)
	{
		// main
		int mainLeft = buttonX - BUTTON_MARGIN;
		int mainRight = buttonX + dialogueActs.size() * (BUTTON_WIDTH + BUTTON_MARGIN);
		int mainBottom = buttonY + BUTTON_HEIGHT + BUTTON_MARGIN;
		int mainTop = buttonY - BUTTON_MARGIN;
		this.mainPos = getPosMap(mainLeft, mainTop, mainRight, mainBottom);

		// edge
		int edgeLeft = baseX - EDGERECT_WIDTH/2;
		int edgeRight = baseX + EDGERECT_WIDTH/2;
		int edgeBottom = baseY;
		int edgeTop = edgeBottom - EDGERECT_HEIGHT;
		this.edgePos = getPosMap(edgeLeft, edgeTop, edgeRight, edgeBottom);

		// middle
		int middleLeft = baseX - EDGERECT_WIDTH;
		int middleRight = baseX + EDGERECT_WIDTH;
		int middleBottom = edgeTop - BUTTON_MARGIN;
		int middleTop = middleBottom - EDGERECT_HEIGHT;
		this.middlePos = getPosMap(middleLeft, middleTop, middleRight, middleBottom);
	}

	private Map<String, Integer> getPosMap(int left, int top, int right, int bottom)
	{
		Map<String, Integer> map = new HashMap<>();
		map.put("left", left);
		map.put("top", top);
		map.put("right", right);
		map.put("bottom", bottom);

		return map;
	}

    /**
     * Draws the popup.
     */
    public void drawPopup(int mouseX, int mouseY, float partialTicks)
    {
    	// main Popup
    	drawRect(mainPos.get("left"), mainPos.get("top"), mainPos.get("right"), mainPos.get("bottom"), BACKGROUND_COLOR);

    	// sub
    	drawRect(edgePos.get("left"), edgePos.get("top"), edgePos.get("right"), edgePos.get("bottom"), BACKGROUND_COLOR);
    	drawRect(middlePos.get("left"), middlePos.get("top"), middlePos.get("right"), middlePos.get("bottom"), BACKGROUND_COLOR);

        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
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
        changeComponentColor(false);
	}

	private void changeComponentColor(Boolean annotating)
	{
        if(this.mc.ingameGUI.getChatGUI() instanceof MyGuiNewChat)
        {
        	MyGuiNewChat guiNewChat = (MyGuiNewChat)this.mc.ingameGUI.getChatGUI();
        	this.component.changeColor(annotating);
        	guiNewChat.replaceChatComponent(componentX, componentY, this.component);
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
