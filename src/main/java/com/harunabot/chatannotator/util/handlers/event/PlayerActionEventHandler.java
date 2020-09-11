package com.harunabot.chatannotator.util.handlers.event;

import java.util.Objects;

import com.harunabot.chatannotator.ChatAnnotator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class PlayerActionEventHandler
{
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
		else
		{
			// Not gimmick
			return;
		}

		ChatAnnotator.CHAT_RECORDER.recordGimmickLog(event.getEntityPlayer(), pos, block.getRegistryName(), isActivated);
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onPressurePlate(NeighborNotifyEvent event)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!(block instanceof BlockPressurePlate)) return;

		boolean isOn = !state.getValue(BlockPressurePlate.POWERED).booleanValue();
		if (isOn) return;

		// Search the players
		EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);


		ChatAnnotator.CHAT_RECORDER.recordGimmickLog(player, pos, block.getRegistryName(), true);

	}
}
