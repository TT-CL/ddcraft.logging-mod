package com.harunabot.chatannotator.annotator.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.harunabot.chatannotator.ChatAnnotator;
import com.harunabot.chatannotator.server.FileOutput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChatRecorder
{
	public static final String STATUS_DIR_NAME = "logs";

	// TODO: チャットアノテーションとの紐付け
	// TODO: reset on dimension change
	private Map<UUID, Map<Integer, ChatStatusJson>> chatStatuses = new HashMap<>();

	private Map<Integer, ArrayList<GimmickLogJson>> gimmickLogs = new HashMap<>();

	public void refreshDimension(int dimension)
	{
		if (!gimmickLogs.containsKey(dimension)) return;

		gimmickLogs.put(dimension, new ArrayList<>());
	}

	public void recordChatStatus(EntityPlayer player, BlockPos playerPos, Vec3d playerLook, BlockPos lookingBlockPos, String lookingBlockName)
	{
		UUID uuid = player.getUniqueID();
		if (!chatStatuses.containsKey(uuid))
		{
			chatStatuses.put(uuid, new HashMap<>());
		}

		 Map<Integer, ChatStatusJson> playerLogs = chatStatuses.get(uuid);
		 // TODO: synchronize id & time with other chatlogs
		 int chatId = playerLogs.size();
		 playerLogs.put(chatId, new ChatStatusJson(chatId, new Date(), playerPos, playerLook, lookingBlockPos, lookingBlockName));

		 outputPlayerJson(player);
	}

	public void recordGimmickLog(EntityPlayer player, BlockPos gimmickPos, ResourceLocation gimmickName, boolean isActivated)
	{
		int dimension = player.dimension;
		if (!gimmickLogs.containsKey(dimension))
		{
			gimmickLogs.put(dimension, new ArrayList<>());
		}

		 ArrayList<GimmickLogJson> dimLog = gimmickLogs.get(dimension);
		 dimLog.add(new GimmickLogJson(gimmickPos, gimmickName, isActivated, player, new Date()));

		 // Output
		 File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		 File jsonFile = new File(dimDir, "gimmickLog.json");
		 FileOutput.outputJson(jsonFile, dimLog);
	}

	protected void outputPlayerJson(EntityPlayer player)
	{
		UUID uuid = player.getUniqueID();
		Map<Integer, ChatStatusJson> jsonMap = chatStatuses.get(uuid);

		File dimDir = ChatAnnotator.dimensionDirectories.get(player.dimension);
		File logDir = new File(dimDir, STATUS_DIR_NAME);
		if (!logDir.exists()) logDir.mkdir();
		File jsonFile = new File(logDir, uuid.toString() + ".json");

		FileOutput.outputJson(jsonFile, jsonMap);
	}
}
