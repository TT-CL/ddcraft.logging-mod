package com.harunabot.chatannotator.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

// Manages and converts serialId to numeral id for each player&dimension
public class ChatIdManagerServer
{
	private Map<Integer, Map<UUID, Map<String, Integer>>> dimensionChatIds;

	public ChatIdManagerServer()
	{
		dimensionChatIds = new HashMap<>();
	}

	public int getId(EntityPlayer player, String serialId)
	{
		int dimension = player.dimension;
		UUID uuid = player.getUniqueID();
		Map<UUID, Map<String, Integer>> dimensionChats = dimensionChatIds.get(dimension);
		Map<String, Integer> playerChats;

		// Player's first chat in the world
		if (!dimensionChats.containsKey(uuid))
		{
			playerChats = new HashMap<>();
			playerChats.put(serialId, 0);
			dimensionChats.put(uuid, playerChats);

			return 0;
		}

		playerChats = dimensionChats.get(uuid);
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

	public void onCreateDimension(int dimension)
	{
		dimensionChatIds.put(dimension, new HashMap<>());
	}

	public void onDestroyDimension(int dimension)
	{
		dimensionChatIds.remove(dimension);
	}

}
