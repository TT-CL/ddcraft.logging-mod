package jp.ac.titech.c.cl.chatannotator.logger.server.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChatStatusJson
{
	public final int chatId;
	public String text;
	public String chatTime;

	// BlockPos playerPos;
	public int playerX;
	public int playerY;
	public int playerZ;

	// Vec3d playerLook;
	public double playerLookX;
	public double playerLookY;
	public double playerLookZ;

	// BlockPos partnerPos;
	public int partnerX;
	public int partnerY;
	public int partnerZ;

	// Vec3d partnerLook;
	public double partnerLookX;
	public double partnerLookY;
	public double partnerLookZ;

	// BlockPos lookingAtPos;
	public int lookingBlockX;
	public int lookingBlockY;
	public int lookingBlockZ;

	public String lookingBlockName = "";

	public ChatStatusJson(
			int chatId, String text, Date chatTime,
			BlockPos playerPos, Vec3d playerLook, BlockPos partnerPos, Vec3d partnerLook)
	{
		this.chatId = chatId;
		this.text = text;
		this.chatTime =  new SimpleDateFormat("HH:mm:ss").format(chatTime);

		playerX = playerPos.getX();
		playerY = playerPos.getY();
		playerZ = playerPos.getZ();

		playerLookX = playerLook.x;
		playerLookY = playerLook.y;
		playerLookZ = playerLook.z;

		if(Objects.nonNull(partnerPos))
		{
			partnerX = partnerPos.getX();
			partnerY = partnerPos.getY();
			partnerZ = partnerPos.getZ();

			partnerLookX = partnerLook.x;
			partnerLookY = partnerLook.y;
			partnerLookZ = partnerLook.z;
		}
	}

	public ChatStatusJson(
			int chatId, BlockPos lookingBlockPos, String lookingBlockName)
	{
		this.chatId = chatId;

		lookingBlockX = lookingBlockPos.getX();
		lookingBlockY = lookingBlockPos.getY();
		lookingBlockZ = lookingBlockPos.getZ();

		this.lookingBlockName = lookingBlockName;
	}

	public void setMessageAndPositionInfo(String chatMessage, Date chatTime,
			BlockPos playerPos, Vec3d playerLook, BlockPos partnerPos, Vec3d partnerLook)
	{
		this.text = chatMessage;
		this.chatTime =  new SimpleDateFormat("HH:mm:ss").format(chatTime);

		playerX = playerPos.getX();
		playerY = playerPos.getY();
		playerZ = playerPos.getZ();

		playerLookX = playerLook.x;
		playerLookY = playerLook.y;
		playerLookZ = playerLook.z;

		if(Objects.nonNull(partnerPos))
		{
			partnerX = partnerPos.getX();
			partnerY = partnerPos.getY();
			partnerZ = partnerPos.getZ();

			partnerLookX = partnerLook.x;
			partnerLookY = partnerLook.y;
			partnerLookZ = partnerLook.z;
		}
	}

	public void setLookingInfo(BlockPos lookingBlockPos, String lookingBlockName)
	{
		lookingBlockX = lookingBlockPos.getX();
		lookingBlockY = lookingBlockPos.getY();
		lookingBlockZ = lookingBlockPos.getZ();

		this.lookingBlockName = lookingBlockName;
	}

}
