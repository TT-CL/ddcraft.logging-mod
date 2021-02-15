package jp.ac.titech.c.cl.chatannotator.server;

import java.util.Objects;
import java.util.UUID;

import jp.ac.titech.c.cl.chatannotator.common.config.ModConfig;
import jp.ac.titech.c.cl.chatannotator.util.handlers.event.ChatEventHandler;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class AlterDedicatedPlayerList extends DedicatedPlayerList
{
	public AlterDedicatedPlayerList(DedicatedServer server)
	{
		super(server);
	}

	@Override
	public void sendMessage(ITextComponent component, boolean isSystem) {
		int messageDim = -10;
		TextComponentAnnotation convertedComponent = TextComponentUtils.getComponentAnnotation(component);
		if (Objects.nonNull(convertedComponent))
		{
			messageDim = convertedComponent.getDimension();
			if (ModConfig.serverOnlyMode || !ModConfig.enableAnnotationLabel)
			{
				// change to normal text component
				if (component instanceof TextComponentTranslation)
				{
					TextComponentTranslation tComponent = (TextComponentTranslation) component;
					Object[] args = tComponent.getFormatArgs();
					convertedComponent.toDefaultStyle();
					args[1] = convertedComponent;
					component = new TextComponentTranslation(tComponent.getKey(), args);
				}
			}
		}

		MinecraftServer server = super.getServerInstance();
		server.sendMessage(component);
		ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
		SPacketChat packetChat = new SPacketChat(component, chattype);

		// SYSTEM
		if (isSystem || messageDim == -10)
		{
			this.sendPacketToAllPlayers(packetChat);
		}
		// CHAT
		else
		{
			this.sendPacketToAllPlayersInDimension(packetChat, messageDim);
		}
	}
}
