package com.beep_boop.Beep.levels;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class LevelManager 
{
	///-----Interfaces-----
	public interface LevelStateListener
	{
		public void stateDidChangeForLevel(String aLevelKey, boolean aState);
	}

	///-----Static Variables-----
	/** Holds if the singleton has been loaded */
	private static boolean loaded = false;
	/** Holds the log tag */
	private static final String TAG = "LevelManager";
	/** Holds the singleton instance of the class */
	public static LevelManager INSTANCE = new LevelManager();

	private static final String FILENAME = "level_completion_data";

	///-----Member Variables-----
	/** Holds the data about each level */
	private HashMap<String, Level> mLevelData;
	/** Holds the level state listeners */
	private ArrayList<LevelStateListener> mLevelStateListeners = new ArrayList<LevelStateListener>();

	///-----Constructors-----
	private LevelManager()
	{

	}

	///-----Public Wrapper Methods-----
	public static void load(Context aContext)
	{
		if (!LevelManager.loaded)
		{
			LevelManager.INSTANCE = new LevelManager();
			LevelManager.INSTANCE.loadPrivate(aContext);
			LevelManager.loaded = true;
		}
	}

	public static boolean getIsLevelComplete(String aLevelKey)
	{
		return LevelManager.INSTANCE.getLevelCompletePrivate(aLevelKey);
	}

	public static void setLevelComplete(String aLevelKey, boolean aComplete, double aTime, int aSteps)
	{
		LevelManager.INSTANCE.setLevelCompletePrivate(aLevelKey, aComplete, aTime, aSteps);
	}

	public static void addLevelStateListener(LevelStateListener aListener)
	{
		LevelManager.INSTANCE.addLevelStateListenerPrivate(aListener);
	}

	public static void removeLevelStateListener(LevelStateListener aListener)
	{
		LevelManager.INSTANCE.removeLevelStateListenerPrivate(aListener);
	}

	public static boolean canPlayLevel(String aLevelKey)
	{
		return LevelManager.INSTANCE.canPlayLevelPrivate(aLevelKey);
	}

	public static Level getLevelForKey(String aLevelKey)
	{
		return LevelManager.INSTANCE.getLevelForKeyPrivate(aLevelKey);
	}

	///-----Private Methods-----
	private void loadPrivate(Context aContext)
	{
		InputStream in = null;
		try 
		{
			in = aContext.getResources().openRawResource(R.raw.levels_test_file);

			this.mLevelData = LevelDataParser.parseFile(in);

			InputStream instream = MyApplication.getAppContext().openFileInput(FILENAME);
			if (instream != null)
			{
				InputStreamReader inputreader = new InputStreamReader(instream); 
				BufferedReader buffreader = new BufferedReader(inputreader); 
				String line = "";
				try
				{
					while ((line = buffreader.readLine()) != null)
					{
						String levelKey = null;
						Boolean completed = false;
						double time = 0;
						int moves = 0;

						int completedIndex = line.indexOf(" ", 0);
						int timeIndex = line.indexOf(" ", completedIndex + 1);
						int movesIndex = line.indexOf(" ", timeIndex + 1);

						levelKey = line.substring(0, completedIndex);
						completed = Boolean.parseBoolean(line.substring(completedIndex + 1, timeIndex));
						time = Double.parseDouble(line.substring(timeIndex + 1, movesIndex));
						moves = Integer.parseInt(line.substring(movesIndex + 1, line.length()));

						this.setLevelCompletePrivate(levelKey, completed, time, moves);
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception i)
		{
			Log.e(LevelManager.TAG, "The IOException was caught.");
			i.printStackTrace();
			if (i.getMessage() != null)
			{
				Log.e(TAG, i.getMessage());
			}
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

	private void save()
	{
		FileOutputStream fos = null;
		try
		{
			fos = MyApplication.getAppContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);

			for (String key : this.mLevelData.keySet())
			{
				Level currentLevel = this.mLevelData.get(key);
				currentLevel.writeToFile(fos);
			}
		}
		catch (IOException e)
		{

		}
		finally
		{
			try
			{
				if (fos != null)
				{
					fos.close();
				}
			}
			catch (IOException i)
			{

			}
		}
	}

	private boolean getLevelCompletePrivate(String aLevelKey)
	{
		boolean result = false;

		if (this.mLevelData.containsKey(aLevelKey))
		{
			result = this.mLevelData.get(aLevelKey).completed;
		}
		else
		{
			//print error message
			Log.w(LevelManager.TAG, "No level for key: " + aLevelKey);
		}

		return result;
	}

	private void setLevelCompletePrivate(String aLevelKey, boolean aComplete, double aTime, int aSteps)
	{
		if (this.mLevelData.containsKey(aLevelKey))
		{
			if (aComplete)
			{
				//store the state
				this.mLevelData.get(aLevelKey).completed = aComplete;

				//store the time & steps
				if (this.mLevelData.get(aLevelKey).time > aTime)
					this.mLevelData.get(aLevelKey).time = aTime;
				if (this.mLevelData.get(aLevelKey).numberOfSteps > aSteps)
					this.mLevelData.get(aLevelKey).numberOfSteps = aSteps;

				//notify listeners
				for (LevelStateListener listener : this.mLevelStateListeners)
				{
					listener.stateDidChangeForLevel(aLevelKey, aComplete);
				}

				//save
				this.save();
			}
		}
		else
		{
			//print error message
			Log.w(LevelManager.TAG, "No level for key: " + aLevelKey);
		}
	}

	private void addLevelStateListenerPrivate(LevelStateListener aListener)
	{
		//check if we already have the listener
		if (!this.mLevelStateListeners.contains(aListener))
		{
			//if not, add the listener
			this.mLevelStateListeners.add(aListener);
		}
	}

	private void removeLevelStateListenerPrivate(LevelStateListener aListener)
	{
		//check if we have the listener
		if (this.mLevelStateListeners.contains(aListener))
		{
			//remove the listener
			this.mLevelStateListeners.remove(aListener);
		}
	}

	private boolean canPlayLevelPrivate(String aLevelKey)
	{
		boolean result = true;
		//check if we have a level with that key
		if (this.mLevelData.containsKey(aLevelKey))
		{
			//check there are required levels
			if (this.mLevelData.get(aLevelKey).requiredLevels != null)
			{
				//go through all required levels
				for (String string : this.mLevelData.get(aLevelKey).requiredLevels)
				{
					//get the state of the level
					boolean requiredState = this.getLevelCompletePrivate(string);
					//if it's not complete, we cannot play
					if (!requiredState)
					{
						//return we cannot play
						result = false;
						break;
					}
				}
			}
		}
		else
		{
			//if not, print an error
			Log.w(LevelManager.TAG, "Invalid level key: " + aLevelKey);
			result = false;
		}

		return result;
	}

	private Level getLevelForKeyPrivate(String aLevelKey)
	{
		Level result = null;
		if (this.mLevelData.containsKey(aLevelKey))
		{
			result = this.mLevelData.get(aLevelKey);
		}
		else
		{
			Log.e(LevelManager.TAG, "No level for key: " + aLevelKey);
		}

		return result;
	}

}
