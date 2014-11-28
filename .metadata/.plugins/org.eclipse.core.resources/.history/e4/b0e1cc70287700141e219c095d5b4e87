package com.beep_boop.Beep.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.beep_boop.Beep.R;

public class WordHandler
{
	///-----Static Variables-----
	/** Holds if the singleton has been loaded */
	private static boolean loaded = false;
	/** Holds the log tag */
	private static final String TAG = "LevelManager";
	/** Holds the singleton instance of the class */
	public static WordHandler INSTANCE;
	
	///-----Member Variables-----
	/** Holds all the word data */
	private Hashtable<String, Hashtable<String, Integer>> mWordData;
	
	private WordHandler()
	{
		
	}
	
	///-----Public Wrapper Methods-----
	public static void load(Context aContext)
	{
		if (!WordHandler.loaded)
		{
			WordHandler.INSTANCE = new WordHandler();
			WordHandler.INSTANCE.loadPrivate(aContext);
			WordHandler.loaded = true;
		}
	}
	
	public static Set<String> getLinksForWord(String aWord)
	{
		return WordHandler.INSTANCE.getLinksForWordPrivate(aWord);
	}
	
	public static Collection<Integer> getCountsForWord(String aWord)
	{
		return WordHandler.INSTANCE.getCountsForWordPrivate(aWord);
	}
	
	///-----Private Methods-----
	private void loadPrivate(Context aContext)
	{
		InputStream in = null;
		try 
		{
			in = aContext.getResources().openRawResource(R.raw.matthew_mcconaughey_zurich);
			
			this.mWordData = PlayScreenParser.parseFile(in);
		}
		catch (Exception i)
		{
			Log.e(WordHandler.TAG, "The IOException was caught.");
		}
		finally 
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					
				}
			}
		}
	}
	
	private Set<String> getLinksForWordPrivate(String aWord)
	{
		Set<String> result = null;
		if (this.mWordData.containsKey(aWord))
		{
			Set<String> keys = this.mWordData.get(aWord).keySet();
			result = keys;
		}
		else
		{
			Log.e(WordHandler.TAG, "No word: " + aWord);
		}
		
		return result;
	}
	
	private Collection<Integer> getCountsForWordPrivate(String aWord)
	{
		Collection<Integer> result = null;
		if (this.mWordData.containsKey(aWord))
		{
			Collection<Integer> values = this.mWordData.get(aWord).values();
			result = values;
		}
		else
		{
			Log.e(WordHandler.TAG, "No word: " + aWord);
		}
		
		return result;
	}
}
