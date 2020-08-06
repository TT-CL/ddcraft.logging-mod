package com.harunabot.chatannotator.annotator;

import java.util.ArrayList;
import java.util.List;

public enum DialogueAct
{
	QUESTION(0, "質問", "question.png", 1.0f, 0.75f, 0.75f),
	SUGGEST (1, "提案", "suggest.png", 0.75f, 1.0f, 0.75f),
	CONVEY  (2, "伝達", "convey.png", 0.75f, 0.75f, 1.0f),
	YES     (3, "はい", "yes.png", 1.0f, 1.0f, 0.5f),
	NO      (4, "いいえ", "no.png", 0.5f, 1.0f, 1.0f),
	GREETING(5, "挨拶", "greeting.png", 0.5f, 0.5f, 1.0f),
	EXCLAMATION(6,"感嘆", "exclamation.png", 0.5f, 1.0f, 0.5f),
	CORRECT   (7, "訂正", "correct.png", 1.0f, 0.5f, 0.5f);

	public static final List<DialogueAct> DIALOGUE_ACTS = new ArrayList<DialogueAct>();

	private final int id;
	private final String name;
	// TODO:  enumとかで絵文字から選べるようにする
	private final String iconFile;

	// Color of button background
	public final float red;
	public final float green;
	public final float blue;

	private DialogueAct(final int id, final String name, String iconFile, float red, float green, float blue)
	{
		this.id = id;
		this.name = name;
		this.iconFile = iconFile;
		this.red = red;
		this.green = green;
		this.blue = blue;
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

	public static DialogueAct convertFromId(int id)
	{
		for(DialogueAct dialogueAct:getList())
		{
			if(dialogueAct.id == id)
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