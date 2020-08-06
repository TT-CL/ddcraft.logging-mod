package com.harunabot.chatannotator.server;

import java.util.Objects;
import com.harunabot.chatannotator.annotator.DialogueAct;

// ゆくゆくはここに全部情報を統一させたいところ…
public class ChatData
{
	public final String senderAnnotation;
	public final String receiverAnnotation;
	public final String senderId;
	public final String time;
	public final String fullMsg;

	public ChatData(DialogueAct senderAnnot, DialogueAct receiverAnnot, String senderId, String time, String fullMsg)
	{
		this.senderAnnotation = senderAnnot.toString();
		this.receiverAnnotation = (Objects.isNull(receiverAnnot)) ? "null" : receiverAnnot.toString();
		this.senderId = senderId;
		this.time = time;
		this.fullMsg = fullMsg;
	}
}
