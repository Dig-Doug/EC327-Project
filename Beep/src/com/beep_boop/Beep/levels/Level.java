package com.beep_boop.Beep.levels;

import java.util.ArrayList;

public class Level
{
	public String levelKey;
	public boolean completed;
	public String toWord;
	public String fromWord;
	public ArrayList<String> requiredLevels;
	
	public Level(String aLevelKey, boolean aCompleted, String aToWord, String aFromWord, ArrayList<String> aRequiredLevels)
	{
		this.levelKey = aLevelKey;
		this.completed = aCompleted;
		this.toWord = aToWord;
		this.fromWord = aFromWord;
		this.requiredLevels = aRequiredLevels;
	}
}
