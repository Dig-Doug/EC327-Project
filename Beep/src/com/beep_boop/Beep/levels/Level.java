package com.beep_boop.Beep.levels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Level
{
	public String levelKey;
	public String nextLevelKey;
	public String toWord;
	public String fromWord;
	public ArrayList<String> requiredLevels;
	public boolean completed;
	public double time;
	public int numberOfSteps;
	public int maxMoves;
	public String fromImage, toImage;
	public String hint;
	public boolean easterEgg;
	
	public Level(String aLevelKey, String aNextLevelKey, String aFromWord, String aToWord, String aFromImage, String aToImage,  int aMaxMoves,
			ArrayList<String> aRequiredLevels, String aHint, boolean aEgg)
	{
		this.levelKey = aLevelKey;
		this.nextLevelKey = aNextLevelKey;
		this.toWord = aToWord;
		this.fromWord = aFromWord;
		this.requiredLevels = aRequiredLevels;
		this.maxMoves = aMaxMoves;
		this.fromImage = aFromImage;
		this.toImage = aToImage;
		
		this.completed = false;
		this.time = Double.MAX_VALUE;
		this.numberOfSteps = Integer.MAX_VALUE;
		
		this.hint = aHint;
		this.easterEgg = aEgg;
	}
	
	public void writeToFile(FileOutputStream aOut)
	{
		try
		{
			aOut.write(this.levelKey.getBytes());
			aOut.write(" ".getBytes());
			aOut.write(Boolean.valueOf(this.completed).toString().getBytes());
			aOut.write(" ".getBytes());
			aOut.write(Double.valueOf(this.time).toString().getBytes());
			aOut.write(" ".getBytes());
			aOut.write(Integer.valueOf(this.numberOfSteps).toString().getBytes());
			aOut.write("\n".getBytes());
		}
		catch (IOException e)
		{
			//@TODO - 
		}
	}
}
