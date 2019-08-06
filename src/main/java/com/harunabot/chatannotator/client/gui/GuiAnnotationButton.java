package com.harunabot.chatannotator.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiAnnotationButton extends GuiButton
{
	public static final int WIDTH = 30;
	public static final int HEIGHT = 15;

	protected DialogueAct dialogueAct;

	public GuiAnnotationButton(int buttonId, int x, int y, DialogueAct dialogueAct)
	{
		super(buttonId, x, y, WIDTH, HEIGHT, I18n.format(dialogueAct.getName()));

		this.dialogueAct = dialogueAct;
	}

	public void activate()
	{
		this.enabled = false;
	}

	public void deactivate()
	{
		this.enabled = true;
	}
}
