package com.harunabot.chatannotator.annotator.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.harunabot.chatannotator.ChatAnnotator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChatRecorder
{
	public static final String STATUS_DIR_NAME = "logs";

	// TODO: チャットアノテーションとの紐付け
	// TODO: reset on dimension change
	private Map<UUID, Map<Integer, ChatStatusJson>> chatStatuses = new HashMap<>();

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

	protected void outputPlayerJson(EntityPlayer player)
	{
		UUID uuid = player.getUniqueID();
		// @JsonSerialize(keyUsing = MapSerializer.class)
		Map<Integer, ChatStatusJson> jsonMap = chatStatuses.get(uuid);

		File dimDir = ChatAnnotator.dimensionDirectories.get(player.dimension);
		File logDir = new File(dimDir, STATUS_DIR_NAME);
		if (!logDir.exists()) logDir.mkdir();

		ObjectMapper mapper = new ObjectMapper();
		File jsonFile = new File(logDir, uuid.toString() + ".json");
		try {
			 mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, jsonMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			System.out.println("Failed to process json");
		} catch (IOException e) {
	    	e.printStackTrace();
			System.out.println("Failed to output JSON");
		}
	}
}
