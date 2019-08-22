package com.android.utils;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class Translator
{
	private static final String API_KEY = "FE55328FE94D3809B4C6F458F1C5E4E655FE47FF";
	private static Language sourceLanguage;
	private static Language targetLanguage;
	private static String text;

	public static void main(String[] args)
	{
		text = IO.read(args[1]);
		Translate.setKey(API_KEY);
		try
		{
			if (args[0].equals("ru"))
			{
				targetLanguage = Language.RUSSIAN;
			}
			else if (args[0].equals("en"))
			{
				targetLanguage = Language.ENGLISH;
			}
			sourceLanguage = Language.AUTO_DETECT;
			String result=Translate.execute(text, sourceLanguage, targetLanguage);
			System.out.println(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String translate(String lang,String fn){
		String result="";
		text = IO.read(fn);
		Translate.setKey(API_KEY);
		try
		{
			targetLanguage=Language.fromString(lang);
			sourceLanguage = Language.AUTO_DETECT;
			result=Translate.execute(text, sourceLanguage, targetLanguage);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
