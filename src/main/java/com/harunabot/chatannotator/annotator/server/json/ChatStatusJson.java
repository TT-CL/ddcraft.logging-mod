package com.harunabot.chatannotator.annotator.server.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChatStatusJson
{
	public final int chatId;
	//public final String chatMessage;
	public final String chatTime;

	// BlockPos playerPos;
	public final int playerX;
	public final int playerY;
	public final int playerZ;

	// Vec3d playerLook;
	public final double playerLookX;
	public final double playerLookY;
	public final double playerLookZ;

	// BlockPos lookingAtPos;
	public final int lookingBlockX;
	public final int lookingBlockY;
	public final int lookingBlockZ;

	public final String lookingBlockName;

	public ChatStatusJson(
			int chatId, /*String chatMessage,*/ Date chatTime,
			BlockPos playerPos, Vec3d playerLook, BlockPos lookingBlockPos, String lookingBlockName)
	{
		this.chatId = chatId;
		//this.chatMessage = chatMessage;
		this.chatTime =  new SimpleDateFormat("HH:mm:ss").format(chatTime);

		playerX = playerPos.getX();
		playerY = playerPos.getY();
		playerZ = playerPos.getZ();

		playerLookX = playerLook.x;
		playerLookY = playerLook.y;
		playerLookZ = playerLook.z;

		lookingBlockX = lookingBlockPos.getX();
		lookingBlockY = lookingBlockPos.getY();
		lookingBlockZ = lookingBlockPos.getZ();

		this.lookingBlockName = lookingBlockName;
	}

}
