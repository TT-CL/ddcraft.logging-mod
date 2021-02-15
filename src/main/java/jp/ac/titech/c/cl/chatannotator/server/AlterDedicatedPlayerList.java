package jp.ac.titech.c.cl.chatannotator.server;

import java.util.UUID;

import jp.ac.titech.c.cl.chatannotator.util.handlers.event.ChatEventHandler;
import jp.ac.titech.c.cl.chatannotator.util.text.TextComponentAnnotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
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
		MinecraftServer server = super.getServerInstance();
		server.sendMessage(component);
		ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
		SPacketChat packetChat = new SPacketChat(component, chattype);

		// SYSTEM
		if (isSystem)
		{
			this.sendPacketToAllPlayers(packetChat);
		}
		// CHAT
		else
		{
			int messageDim = ChatEventHandler.getChatDimension(component);
			if (messageDim == -10)
			{
				this.sendPacketToAllPlayers(packetChat);
			}
			else
			{
				// change to normal text component
				this.sendPacketToAllPlayersInDimension(packetChat, messageDim);
			}
		}
	}
}
