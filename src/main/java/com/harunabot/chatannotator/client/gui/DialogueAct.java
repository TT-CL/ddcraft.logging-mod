package com.harunabot.chatannotator.client.gui;

import java.util.ArrayList;
import java.util.List;

public enum DialogueAct
{
	QUESTION(0, "質問", "question.png"),
	SUGGEST (1, "提案", "suggest.png"),
	CONVEY  (2, "伝達", "convey.png"),
	YES     (3, "はい", "yes.png"),
	NO      (4, "いいえ", "no.png"),
	GREETING(5, "挨拶", "greeting.png"),
	EXCLAMATION(6,"感嘆", "exclamation.png"),
	CORRECT   (7, "訂正", "correct.png");

	public static final List<DialogueAct> DIALOGUE_ACTS = new ArrayList<DialogueAct>();

	private final int id;
	private final String name;
	// TODO:  enumとかで絵文字から選べるようにする
	private final String iconFile;

	private DialogueAct(final int id, final String name, String iconFile)
	{
		this.id = id;
		this.name = name;
		this.iconFile = iconFile;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getIconFile()
	{
		return iconFile;
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