package jp.ac.titech.c.cl.chatannotator.logger.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jp.ac.titech.c.cl.chatannotator.ChatAnnotator;
import jp.ac.titech.c.cl.chatannotator.logger.server.json.ChatStatusJson;
import jp.ac.titech.c.cl.chatannotator.logger.server.json.GimmickLogJson;
import jp.ac.titech.c.cl.chatannotator.server.FileOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChatRecorder
{
	public static final String STATUS_DIR_NAME = "logs";
	public static final String GIMMICK_FILE_NAME = "gimmickLog.json";

	// TODO: チャットアノテーションとの紐付け
	private Map<Integer, Map<UUID, Map<Integer, ChatStatusJson>>> dimensionChatStatuses = new HashMap<>();

	private Map<Integer, ArrayList<GimmickLogJson>> gimmickLogs = new HashMap<>();

	public void onCreateDimension(int dimension)
	{
		dimensionChatStatuses.put(dimension, new HashMap<>());
		gimmickLogs.put(dimension, new ArrayList<>());

		File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		FileOutput.createFile(new File(dimDir, GIMMICK_FILE_NAME));
	}

	public void onDestroyDimension(int dimension)
	{
		dimensionChatStatuses.remove(dimension);
		gimmickLogs.remove(dimension);
	}

	public void recordChatStatus(EntityPlayer player, String serialId, BlockPos playerPos, Vec3d playerLook, BlockPos lookingBlockPos, String lookingBlockName)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<Integer, ChatStatusJson>> chatStatuses = dimensionChatStatuses.get(dimension);

		if (!chatStatuses.containsKey(uuid))
		{
			chatStatuses.put(uuid, new HashMap<>());
		}

		 Map<Integer, ChatStatusJson> playerLogs = chatStatuses.get(uuid);
		 int chatId = ChatAnnotator.CHAT_ID_MANAGER_SERVER.getId(player, serialId);
		 playerLogs.put(chatId, new ChatStatusJson(chatId, new Date(), playerPos, playerLook, lookingBlockPos, lookingBlockName));

		 outputPlayerJson(player);
	}

	public void recordGimmickLog(EntityPlayer player, BlockPos gimmickPos, ResourceLocation gimmickName, boolean isActivated)
	{
		int dimension = player.dimension;

		 ArrayList<GimmickLogJson> dimLog = gimmickLogs.get(dimension);
		 dimLog.add(new GimmickLogJson(gimmickPos, gimmickName, isActivated, player, new Date()));

		 // Output
		 File dimDir = ChatAnnotator.dimensionDirectories.get(dimension);
		 File jsonFile = new File(dimDir, GIMMICK_FILE_NAME);
		 FileOutput.outputJson(jsonFile, dimLog);
	}

	protected void outputPlayerJson(EntityPlayer player)
	{
		UUID uuid = player.getUniqueID();
		int dimension = player.dimension;
		Map<Integer, ChatStatusJson> jsonMap = dimensionChatStatuses.get(dimension).get(uuid);

		File dimDir = ChatAnnotator.dimensionDirectories.get(player.dimension);
		File logDir = new File(dimDir, STATUS_DIR_NAME);
		if (!logDir.exists()) logDir.mkdir();
		File jsonFile = new File(logDir, uuid.toString() + ".json");

		FileOutput.outputJson(jsonFile, jsonMap);
	}
}
