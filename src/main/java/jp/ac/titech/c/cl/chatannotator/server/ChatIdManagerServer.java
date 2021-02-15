package jp.ac.titech.c.cl.chatannotator.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

import javax.annotation.Nullable;

import jp.ac.titech.c.cl.chatannotator.network.ChatIdMessage;
import net.minecraft.entity.player.EntityPlayer;

// Manages and converts serialId to numeral id for each player&dimension
public class ChatIdManagerServer
{

	// dimension -> player uuid -> serial id -> numeral id
	private Map<Integer, Map<UUID, Map<String, Integer>>> dimensionChatIds;
	// chats notified by ChatIdMessage but not yet captured by ServerChatEvent
	private Map<Integer, Map<UUID, Map<String, Queue<String>>>> notifiedChatsToSerial;

	public ChatIdManagerServer()
	{
		dimensionChatIds = new HashMap<>();
		notifiedChatsToSerial = new HashMap<>();
	}

	// Get numeral id from serial id
	public int getId(EntityPlayer player, String serialId)
	{
		Map<String, Integer> playerChats = findOrCreatePlayerChatIds(player);

		// serialId already registered
		if (playerChats.containsKey(serialId))
		{
			return playerChats.get(serialId);
		}

		// register serialId and get new numeral id
		int nextId = playerChats.size();
		playerChats.put(serialId, nextId);

		return nextId;
	}

	// For HandlerChatIdMessage: connect full message & serialId
	public void processChatIdMessage(String serialId, String msg, EntityPlayer player)
	{
		Map<String, Queue<String>> playerNotifiedChats = findOrCreatePlayerNotifiedChats(player);

		// register message
		putNotifiedChatMapping(msg, serialId, playerNotifiedChats);

		return;
	}

	// For ChatEventHandler: fix numeral id for the message
	public int getIdOnServerChat(String message, EntityPlayer player)
	{
		// shorten message to match ChatIdMessage
		if (message.length() > ChatIdMessage.MAX_MESSAGE_LENGTH) {
			message = message.substring(0, ChatIdMessage.MAX_MESSAGE_LENGTH);
		}

		Map<String, Queue<String>> playerNotifiedChats = findOrCreatePlayerNotifiedChats(player);
		String serialId = getNotifiedChatId(message, playerNotifiedChats);

		if (Objects.isNull(serialId)) // && ModConfig.serverSideOnly
		{
			// Client side not modded: Serial id hasn't notified by ChatIdMessage.
			Map<String, Integer> playerChats = findOrCreatePlayerChatIds(player);
			int numeralId = playerChats.size();

			// Register dummy serial id instead
			serialId = DUMMY_ID_PREFIX + Integer.toString(numeralId);
			//putNotifiedChatMapping(message, serialId, playerNotifiedChats);
		}
		else
		{
			// Unregister notified message if exist
			removeNotifiedChatMapping(message, playerNotifiedChats);
		}

		// Fix numeral id
		int numeralId = getId(player, serialId);
		return numeralId;
	}

	public void onCreateDimension(int dimension)
	{
		dimensionChatIds.put(dimension, new HashMap<>());
		notifiedChatsToSerial.put(dimension, new HashMap<>());
	}

	public void onDestroyDimension(int dimension)
	{
		dimensionChatIds.remove(dimension);
		notifiedChatsToSerial.remove(dimension);
	}

	// get appropriate map from dimensionChatIds
	private Map<String, Integer> findOrCreatePlayerChatIds(EntityPlayer player)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<String, Integer>> dimensionChats = dimensionChatIds.get(dimension);

		// Player's first chat in the world
		if (!dimensionChats.containsKey(uuid))
		{
			Map<String, Integer> playerChats = new HashMap<>();
			dimensionChats.put(uuid, playerChats);

			return playerChats;
		}

		return dimensionChats.get(uuid);
	}

	// get appropriate map from notifiedChatsToSerial
	private Map<String, Queue<String>> findOrCreatePlayerNotifiedChats(EntityPlayer player)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<String, Queue<String>>> dimensionChats = notifiedChatsToSerial.get(dimension);

		// Player's first chat in the world
		if (!dimensionChats.containsKey(uuid))
		{
			Map<String, Queue<String>> playerChats = new HashMap<>();
			dimensionChats.put(uuid, playerChats);

			return playerChats;
		}

		return dimensionChats.get(uuid);
	}

	// put new message->serialId mapping to notifiedChatSerial
	private static void putNotifiedChatMapping(String msg, String serialId, Map<String, Queue<String>> playerChats)
	{
		Queue<String> idList = playerChats.get(msg);
		if(Objects.isNull(idList))
		{
			idList = new LinkedList<>();
			playerChats.put(msg, idList);
		}

		idList.add(serialId);
	}

	// remove first mapping from notifiedChatSerial
	private static void removeNotifiedChatMapping(String msg, Map<String, Queue<String>> playerChats)
	{
		Queue<String> idList = playerChats.get(msg);
		if(Objects.isNull(idList)) return;

		idList.poll();
		if(idList.isEmpty())
		{
			// delete the list if there's no more mapping
			playerChats.remove(msg);
		}
	}

	// get first serial id mapped from the message
	@Nullable
	private static String getNotifiedChatId(String msg, Map<String, Queue<String>> playerChats)
	{
		Queue<String> idList = playerChats.get(msg);
		if(Objects.isNull(idList)) return null;

		return idList.peek();
	}

}
