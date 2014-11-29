package com.beep_boop.Beep.levels;

import java.util.ArrayList;

public class Level
{
	public String levelKey;
	public String toWord;
	public String fromWord;
	public ArrayList<String> requiredLevels;
	public boolean completed;
	public double time;
	public int numberOfSteps;
	
	public Level(String aLevelKey, boolean aCompleted, String aFromWord, String aToWord, 
			ArrayList<String> aRequiredLevels, double aTime, int aNumberOfSteps)
	{
		this.levelKey = aLevelKey;
		this.completed = aCompleted;
		this.toWord = aToWord;
		this.fromWord = aFromWord;
		this.requiredLevels = aRequiredLevels;
		this.time = aTime;
		this.numberOfSteps = aNumberOfSteps;
	}
}
