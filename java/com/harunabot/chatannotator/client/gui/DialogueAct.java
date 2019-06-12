package com.harunabot.chatannotator.client.gui;

import java.util.ArrayList;
import java.util.List;

public enum DialogueAct
{
	QUESTION(0,"質問"),
	SUGGEST (1,"提案"),
	REQUEST (2,"要求"),
	ACCEPT  (3, "承諾"),
	DENY    (4, "拒否"),
	CONVEY  (5, "伝達");

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
		DialogueAct[] array = {QUESTION, SUGGEST, REQUEST, ACCEPT, DENY, CONVEY};
		List<DialogueAct> list = new ArrayList<DialogueAct>();
		for(int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
}