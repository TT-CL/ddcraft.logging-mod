package jp.ac.titech.c.cl.chatannotator.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


// Manages the serial Id of the unsent chats
public class ChatIdManagerClient
{
	// how long the substr of the msg in serial ids should be
	public static final int ID_SUBSTR_LENGTH = 4;

	// messages that are not sent but have id
	private Map<String, String> messageIds;

	public ChatIdManagerClient()
	{
		messageIds = new HashMap<>();
	}

	public String getId(String msg)
	{
		if (messageIds.containsKey(msg)) {
			return messageIds.get(msg);
		}

		String id = getChatSerialId(msg);
		messageIds.put(msg, id);

		return id;
	}

	public void onSendChatMessage(String msg)
	{
		messageIds.remove(msg);
	}

	// return SerialId for a chat as a date
	protected static String getChatSerialId(String msg)
	{
		String subMsg = msg.substring(0, Math.min(ID_SUBSTR_LENGTH, msg.length()));
		String date = new SimpleDateFormat("ddHHmmss").format(new Date());

		return date + subMsg;
	}
}
