package com.harunabot.chatannotator.annotator.handler;

import java.util.Objects;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.annotator.network.message.PlayerStateMessage;
import com.harunabot.chatannotator.util.handlers.ChatAnnotatorPacketHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class PlayerActionEventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientChat(ClientChatEvent event)
	{
		String msg = event.getMessage();
		if (msg.startsWith("/"))
		{
			// ignore command
			return;
		}

		String chatId = ChatAnnotator.CHAT_ID_MANAGER_CLIENT.getId(msg);

		Minecraft mc = Minecraft.getMinecraft();
		ChatAnnotatorPacketHandler.sendToServer(new PlayerStateMessage(chatId, mc.player, mc.world, mc.getRenderPartialTicks()));

		ChatAnnotator.CHAT_ID_MANAGER_CLIENT.onSendChatMessage(msg);
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onRightClickBlock(RightClickBlock event)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (Objects.isNull(block)) return;

		boolean isActivated;

		if (block instanceof BlockDoor)
		{
			// skip iron door
			if (state.getMaterial() == Material.IRON) return;

			BlockDoor door = (BlockDoor)block;
			isActivated = !door.isOpen(world, pos);
		}
		else if (block instanceof BlockButton)
		{
			boolean isOn = state.getValue(BlockButton.POWERED).booleanValue();
			// skip if already on
			if(isOn) return;

			// button is activation only
			isActivated = true;
		}
		else if (block instanceof BlockLever)
		{
			isActivated = !state.getValue(BlockLever.POWERED).booleanValue();
		}
		/*
		 * TODO: catch pressure plates too
		else if (block instanceof BlockPressurePlate)
		{

		}
		*/
		else
		{
			// Not gimmick
			return;
		}

		ChatAnnotator.CHAT_RECORDER.recordGimmickLog(event.getEntityPlayer(), pos, block.getRegistryName(), isActivated);
	}
}
