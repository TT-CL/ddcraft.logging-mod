package com.harunabot.chatannotator.annotator.client.gui;

import com.harunabot.chatannotator.annotator.DialogueAct;
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

	public final DialogueAct dialogueAct;

	public GuiAnnotationButton(int buttonId, int x, int y, DialogueAct dialogueAct)
	{
		super(buttonId, x, y, WIDTH, HEIGHT, I18n.format(dialogueAct.getName()));

		this.dialogueAct = dialogueAct;
		this.ICON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "texture/gui/emoji/" + dialogueAct.getIconFile());
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
        if (this.visible)
        {
        	// draw button(almost the same as the parent, except for color;
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(dialogueAct.red, dialogueAct.green, dialogueAct.blue, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);

            // draw icon
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
