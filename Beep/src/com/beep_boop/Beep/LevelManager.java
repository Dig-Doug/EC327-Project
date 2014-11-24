package com.beep_boop.Beep;

import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;

public class LevelManager 
{
	///-----Interfaces-----
	public interface LevelStateListener
	{
		public void stateDidChangeForLevel(String aLevelKey, boolean aState);
	}
	
	///-----Static Variables-----
	/** Holds the log tag */
	private static final String TAG = "LevelManager";
	/** Holds the singleton instance of the class */
	public static LevelManager INSTANCE = new LevelManager();
	
	///-----Member Variables-----
	/** Holds the completion state for each level */
	private Hashtable<String, Boolean> mLevelStates = new Hashtable<String, Boolean>();
	/** Holds the level state listeners */
	private ArrayList<LevelStateListener> mLevelStateListeners = new ArrayList<LevelStateListener>();
	
	///-----Constructors-----
	private LevelManager()
	{
		//@TODO - load level states
	}
	
	///-----Public Wrapper Methods-----
	public static boolean getIsLevelComplete(String aLevelKey)
	{
		return LevelManager.INSTANCE.getLevelCompletePrivate(aLevelKey);
	}
	
	public static void setLevelComplete(String aLevelKey, boolean aComplete)
	{
		LevelManager.INSTANCE.setLevelCompletePrivate(aLevelKey, aComplete);
	}
	
	public static void addLevelStateListener(LevelStateListener aListener)
	{
		LevelManager.INSTANCE.addLevelStateListenerPrivate(aListener);
	}
	
	public static void removeLevelStateListener(LevelStateListener aListener)
	{
		LevelManager.INSTANCE.removeLevelStateListenerPrivate(aListener);
	}
	
	///-----Private Methods-----
	private void save()
	{
		//@TODO - save level states
	}
	
	private boolean getLevelCompletePrivate(String aLevelKey)
	{
		boolean result = false;
		
		//@TODO
		if (this.mLevelStates.containsKey(aLevelKey))
		{
			result = this.mLevelStates.get(aLevelKey);
		}
		else
		{
			//print error message
			Log.w(LevelManager.TAG, "No level for key: " + aLevelKey);
			
			//set random value for testing
			result = Math.random() < 0.5;
			this.mLevelStates.put(aLevelKey, result);
		}
		
		return result;
	}
	
	private void setLevelCompletePrivate(String aLevelKey, boolean aComplete)
	{
		//save the state
		this.mLevelStates.put(aLevelKey, aComplete);
		
		//notify listeners
		for (LevelStateListener listener : this.mLevelStateListeners)
		{
			listener.stateDidChangeForLevel(aLevelKey, aComplete);
		}
		
		this.save();
	}
	
	private void addLevelStateListenerPrivate(LevelStateListener aListener)
	{
		if (!this.mLevelStateListeners.contains(aListener))
		{
			this.mLevelStateListeners.add(aListener);
		}
	}
	
	private void removeLevelStateListenerPrivate(LevelStateListener aListener)
	{
		if (this.mLevelStateListeners.contains(aListener))
		{
			this.mLevelStateListeners.remove(aListener);
		}
	}
	
}