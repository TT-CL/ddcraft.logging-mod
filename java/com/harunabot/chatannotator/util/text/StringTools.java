package com.harunabot.chatannotator.util.text;

public class StringTools
{
    public static String printAllCharacters(String str)
    {
    	for(char c: str.toCharArray())
    	{
    		System.out.println(c + " : " + Integer.toHexString(c));
    	}
    	return str;
    }

    /**
     * Delete '§.' in the string
     */
    public static String deleteIllegalCharacters(String str)
    {
    	String ret = "";

    	for(int i=0; i<str.length(); i++)
    	{
    		char c = str.charAt(i);
    		if(c == '§') {
    			// Skip next char
    			i++;
    			continue;
    		}
    		ret += c;
    	}

    	return ret;
    }
}
