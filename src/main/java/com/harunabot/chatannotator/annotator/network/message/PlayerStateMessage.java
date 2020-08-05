package com.harunabot.chatannotator.annotator.network.message;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/*
 * A Message from Client side to tell the player status on chat.
 */
public class PlayerStateMessage implements IMessage
{
	private String serialId;

	private BlockPos playerPos;
	// vector of where player is looking
	private Vec3d playerLook;

	private BlockPos lookingAtPos;
	private String lookingAtName;

	// inventory
	// inhand

	public PlayerStateMessage()
	{
	}

	public PlayerStateMessage(String serialId, EntityPlayer player, World world, float partialTicks)
	{
		this.serialId = serialId;

		playerPos = player.getPosition();
		playerLook = player.getLook(partialTicks);

		lookingAtPos = player.rayTrace(100, partialTicks).getBlockPos();
		IBlockState lookingBlockState = world.getBlockState(lookingAtPos);
		lookingAtName = lookingBlockState.getBlock().getRegistryName().toString();
	}

	public String getSerialId()
	{
		return serialId;
	}

	public BlockPos getPlayerPos()
	{
		return playerPos;
	}

	public Vec3d getPlayerLook()
	{
		return playerLook;
	}


	public BlockPos getLookingAtPos()
	{
		return lookingAtPos;
	}

	public String getLookingAtName()
	{
		return lookingAtName;
	}


	@Override
	public void fromBytes(ByteBuf buf)
	{
		int strlen;

		strlen = buf.readInt();
		serialId = buf.readCharSequence(strlen, Charset.defaultCharset()).toString();
		playerPos = bytesToPos(buf);
		playerLook = bytesToVec3d(buf);
		lookingAtPos = bytesToPos(buf);
		strlen = buf.readInt();
		lookingAtName = buf.readCharSequence(strlen, Charset.defaultCharset()).toString();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(serialId.length());
		buf.writeCharSequence(serialId, Charset.defaultCharset());

		buf.writeInt(playerPos.getX());
		buf.writeInt(playerPos.getY());
		buf.writeInt(playerPos.getZ());

		buf.writeDouble(playerLook.x);
		buf.writeDouble(playerLook.y);
		buf.writeDouble(playerLook.z);

		buf.writeInt(lookingAtPos.getX());
		buf.writeInt(lookingAtPos.getY());
		buf.writeInt(lookingAtPos.getZ());

		buf.writeInt(lookingAtName.length());
		buf.writeCharSequence(lookingAtName, Charset.defaultCharset());
	}

	private static BlockPos bytesToPos(ByteBuf buf)
	{
		int x,y,z;
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

		return new BlockPos(x, y, z);
	}

	private static Vec3d bytesToVec3d(ByteBuf buf)
	{
		double x,y,z;
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();

		return new Vec3d(x, y, z);
	}

}