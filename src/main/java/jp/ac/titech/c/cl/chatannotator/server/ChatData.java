package jp.ac.titech.c.cl.chatannotator.server;

import java.util.Objects;

import jp.ac.titech.c.cl.chatannotator.annotator.DialogueAct;

public class ChatData
{
	public final String text;
	public final String senderId;
	public final int senderChatId;
	public final String senderAnnotation;
	public final String receiverAnnotation;
	public final String time;


	public ChatData(DialogueAct senderAnnot, DialogueAct receiverAnnot, String senderId, String time, String fullMsg, int senderChatId)
	{
		this.senderAnnotation = senderAnnot.toString();
		this.receiverAnnotation = (Objects.isNull(receiverAnnot)) ? "null" : receiverAnnot.toString();
		this.senderId = senderId;
		this.time = time;
		this.text = fullMsg;
		this.senderChatId = senderChatId;
	}
}
