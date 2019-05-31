package com.harunabot.chatannotator.util.handlers;

import com.harunabot.chatannotator.client.gui.chat.GuiAnnotationButton;

import com.harunabot.chatannotator.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import scala.reflect.internal.Trees.New;
import scala.tools.nsc.backend.icode.Members.IField;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == Reference.GUI_CHAT_ANNOTATOR)
		{
			return new GuiAnnotationButton();
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == Reference.GUI_CHAT_ANNOTATOR) {
			return new GuiAnnotationButton();
		}
		return null;
	}

}
