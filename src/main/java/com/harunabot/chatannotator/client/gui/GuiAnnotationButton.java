package com.harunabot.chatannotator.client.gui;

import com.harunabot.chatannotator.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiAnnotationButton extends GuiButton
{
	public static final int WIDTH = 40;
	public static final int HEIGHT = 15;
	protected static final int ICON_SIZE = 8;

	protected final ResourceLocation ICON_TEXTURE;

	protected DialogueAct dialogueAct;

	public GuiAnnotationButton(int buttonId, int x, int y, DialogueAct dialogueAct)
	{
		super(buttonId, x, y, WIDTH, HEIGHT, I18n.format(dialogueAct.getName()));

		this.dialogueAct = dialogueAct;
		ICON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "texture/gui/emoji/" + dialogueAct.getIconFile());
	}

	public void activate()
	{
		this.enabled = false;
	}

	public void deactivate()
	{
		this.enabled = true;
	}

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		if (this.visible)
		{
			int scale = 32;
			double ratio = 1.0/scale;
			int margin = (HEIGHT - ICON_SIZE)/2;
			mc.getTextureManager().bindTexture(ICON_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.scale(ratio, ratio, ratio);
			this.drawTexturedModalRect((this.x + margin) * scale, (this.y + margin) * scale, 0, 0, ICON_SIZE * scale, ICON_SIZE * scale);
			GlStateManager.popMatrix();
		}
    }

    @Override
    /**
     * Rendered the text in the right part of the button
     */
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
		super.drawCenteredString(fontRendererIn, text, x + 5, y, color);
    }
}
