package com.harunabot.chatannotator.client.gui;

import java.util.ArrayList;
import java.util.List;

public enum DialogueAct
{
	QUESTION(0, "質問"),
	SUGGEST (1, "提案"),
	GREETING(2, "挨拶"),
	YES     (3, "はい"),
	NO      (4, "いいえ"),
	CONVEY  (5, "伝達"),
	EXCLAMATION(6,"感嘆"),
	CORRECT   (7, "訂正");

	public static final List<DialogueAct> DIALOGUE_ACTS = new ArrayList<DialogueAct>();

	private final int id;
	private final String name;

	private DialogueAct(final int id, final String name)
	{
		this.id = id;
		this.name = name;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public static DialogueAct convertFromName(String name)
	{
		for(DialogueAct dialogueAct:getList())
		{
			if(dialogueAct.name.equals(name))
			{
				return dialogueAct;
			}
		}
		return null;
	}

	public static List<DialogueAct> getList()
	{
		DialogueAct[] array = {QUESTION, SUGGEST, DialogueAct.GREETING,YES,DialogueAct.NO, CONVEY,EXCLAMATION, CORRECT};
		List<DialogueAct> list = new ArrayList<DialogueAct>();
		for(int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
}