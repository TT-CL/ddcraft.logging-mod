package com.harunabot.chatannotator.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.harunabot.chatannotator.network.ChatIdMessage;

import net.minecraft.entity.player.EntityPlayer;

// Manages and converts serialId to numeral id for each player&dimension
public class ChatIdManagerServer
{
	private Map<Integer, Map<UUID, Map<String, Integer>>> dimensionChatIds;

	// chats notified by ChatIdMessage but not yet captured by ServerChatEvent
	private Map<Integer, Map<UUID, Map<String, String>>> notifiedChatsToSerial;

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
		Map<String, String> playerNotifiedChats = findOrCreatePlayerNotifiedChats(player);

		// message already exists -> something wrong?  no registration and return
		if (playerNotifiedChats.containsKey(msg))
		{
			String id = playerNotifiedChats.get(msg);

			if (id == "") {
				// Arrived after ServerChatEvent maybe?
			} else {
				// repeated same chats
			}
			return;
		}

		// register message
		playerNotifiedChats.put(msg, serialId);

		return;
	}

	// For ChatEventHandler: gets numeral Id from
	public int getIdOnServerChat(String message, EntityPlayer player)
	{
		// shorten message to match ChatIdMessage
		if (message.length() > ChatIdMessage.MAX_MESSAGE_LENGTH) {
			message = message.substring(0, ChatIdMessage.MAX_MESSAGE_LENGTH);
		}

		Map<String, String> playerChats = findOrCreatePlayerNotifiedChats(player);
		String serialId = playerChats.get(message);

		if (Objects.isNull(serialId))
		{
			// Called before ChatIdPakcet maybe? return next numeral Id for the player
			playerChats.put(message, "");
			Map<String, Integer> arrivedChats = findOrCreatePlayerChatIds(player);
			return arrivedChats.size();
		}

		int id = getId(player, serialId);
		playerChats.remove(message);

		return id;
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

	// get appropriate map from dimensionChatIds;
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
	private Map<String, String> findOrCreatePlayerNotifiedChats(EntityPlayer player)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<String, String>> dimensionChats = notifiedChatsToSerial.get(dimension);

		// Player's first chat in the world
		if (!dimensionChats.containsKey(uuid))
		{
			Map<String, String> playerChats = new HashMap<>();
			dimensionChats.put(uuid, playerChats);

			return playerChats;
		}

		return dimensionChats.get(uuid);
	}
}
