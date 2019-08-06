package com.harunabot.chatannotator.client.gui;


import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.harunabot.chatannotator.util.text.TextComponentAnnotation;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * GuiUtilRenderComponents extended for TextComponentAnnotation
 */
@SideOnly(Side.CLIENT)
class MyGuiUtilRenderComponents extends GuiUtilRenderComponents
{
    public static List<ITextComponent> splitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean p_178908_3_, boolean forceTextColor)
    {
        int i = 0;
        ITextComponent itextcomponent = new TextComponentString("");
        List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
        List<ITextComponent> list1 = Lists.newArrayList(textComponent);

        for (int j = 0; j < list1.size(); ++j)
        {
            ITextComponent itextcomponent1 = list1.get(j);
            String s = itextcomponent1.getUnformattedComponentText();
            boolean flag = false;

            // Separate component if newline char is found
            if (s.contains("\n"))
            {
                int k = s.indexOf(10);
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ITextComponent itextcomponent2 = createPartComponent(s1, itextcomponent1);
                System.out.println(itextcomponent2);
                list1.add(j + 1, itextcomponent2);
                flag = true;
            }

            String s4 = removeTextColorsIfConfigured(itextcomponent1.getStyle().getFormattingCode() + s, forceTextColor);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRendererIn.getStringWidth(s5);

            ITextComponent textcomponentstring = createPartComponent(s5, itextcomponent1);

            // Separate component if over-length
            if (i + i1 > maxTextLenght)
            {
                String s2 = fontRendererIn.trimStringToWidth(s4, maxTextLenght - i, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && !s3.isEmpty())
                {
                    int l = s2.lastIndexOf(32);

                    if (l >= 0 && fontRendererIn.getStringWidth(s4.substring(0, l)) > 0)
                    {
                        s2 = s4.substring(0, l);

                        if (p_178908_3_)
                        {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    }
                    else if (i > 0 && !s4.contains(" "))
                    {
                        s2 = "";
                        s3 = s4;
                    }

                    s3 = FontRenderer.getFormatFromString(s2) + s3; //Forge: Fix chat formatting not surviving line wrapping.

                    ITextComponent textcomponentstring1 = createPartComponent(s3, itextcomponent1);
                    // System.out.println(textcomponentstring1);
                    list1.add(j + 1, textcomponentstring1);
                }

                i1 = fontRendererIn.getStringWidth(s2);
                textcomponentstring = createPartComponent(s2, itextcomponent1);
                flag = true;
            }

            if (i + i1 <= maxTextLenght)
            {
                i += i1;
                itextcomponent.appendSibling(textcomponentstring);
            }
            else
            {
                flag = true;
            }

            // Newline
            if (flag)
            {
                // System.out.println(itextcomponent);
                list.add(itextcomponent);
                i = 0;
                itextcomponent = new TextComponentString("");
            }
        }

        list.add(itextcomponent);

        return list;
    }

	private static ITextComponent createPartComponent(String s, ITextComponent baseComponent)
    {
    	ITextComponent component;

    	if(baseComponent instanceof TextComponentAnnotation)
		{
    		// TextComponentAnnotation
    		component = ((TextComponentAnnotation)baseComponent).createPartialCopy(s);
    		if(Objects.nonNull(component))
    		{
    			return component;
    		}

    		// Creation Error if null
    		System.err.println("Can't create partial Copy of TextComponentAnnotation: \"" + s + "\" for " + baseComponent.toString());
    	}

		// Default
        component = new TextComponentString(s);
        component.setStyle(baseComponent.getStyle().createShallowCopy());

    	return component;
    }
}
