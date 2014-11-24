package com.beep_boop.Beep.Game;

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
	///-----Member Variables-----
	/** Tag for logging */
	private static final String TAG = "PlayScreenActivity";
	/** Holds all the word data */
	private Hashtable<String, Hashtable<String, Integer>> mWordData;
	
	public WordHandler(Context aContext)
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
	
	public Set<String> getLinksForWord(String aWord)
	{
		Set<String> result = null;
		if (mWordData.contains(aWord))
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
	
	public Collection<Integer> getCountsForWord(String aWord)
	{
		Collection<Integer> result = null;
		if (mWordData.contains(aWord))
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
