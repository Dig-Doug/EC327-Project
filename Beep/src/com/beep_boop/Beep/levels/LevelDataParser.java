package com.beep_boop.Beep.levels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class LevelDataParser
{
	private static final String TAG = "LevelRequirementParser";
	/** XML namespace used in the file. */
	private static final String NAMESPACE = null;

	private static final String TAG_LEVELS = "levels";
	private static final String TAG_LEVEL = "level";
	private static final String TAG_LEVEL_ATRB_KEY = "key";
	private static final String TAG_REQUIRED_LEVELS = "requires";
	private static final String TAG_REQUIRED_LEVEL = "req";
	private static final String TAG_FROM_WORD = "from";
	private static final String TAG_TO_WORD = "to";
	private static final String TAG_NEXT_LEVEL = "next";
	private static final String TAG_MAX_MOVES = "max";
	private static final String TAG_FROM_IMAGE = "fromImage";
	private static final String TAG_TO_IMAGE = "toImage";
	private static final String TAG_HINT = "hint";
	private static final String TAG_EGG = "isEgg";

	///-----Constructors-----
	// None

	///-----Functions-----
	public static HashMap<String, Level> parseFile(InputStream aIn) 
	{
		HashMap<String, Level> results = null;
		//Set up the Parser
		try 
		{
			//Create a parser
			XmlPullParser aParser = Xml.newPullParser();
			aParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//We set the input to our parser as the input file
			aParser.setInput(aIn, null);
			//Advance our parser to the next tag
			aParser.nextTag();
			//Call the parseNodes function to obtain an arraylist of our nodes
			results = parseLevels(aParser);
		} 
		catch (XmlPullParserException e) 
		{
			Log.e(TAG, "The XmlPullParserException was caught.");
			e.printStackTrace();
			if (e.getMessage() != null)
			{
				Log.e(TAG, e.getMessage());
			}
		} 
		catch (IOException i) 
		{
			Log.e(TAG, "The IOException was caught.");
			i.printStackTrace();
			if (i.getMessage() != null)
			{
				Log.e(TAG, i.getMessage());
			}
		} 
		finally 
		{
			try 
			{
				aIn.close();
			} 
			catch (IOException i) 
			{
				Log.e(TAG, "The IOException was caught from the aIn.close().");
			}
		}

		return results;
	}

	private static HashMap<String, Level> parseLevels(XmlPullParser aParser) throws XmlPullParserException, IOException 
	{
		//Parses the content of a "nodes"
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_LEVELS);
		HashMap<String, Level> levelData = new HashMap<String,Level>();
		//As long as we are not at an end-tag we will keep creating nodes
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = aParser.getName();
			if (name.equals(LevelDataParser.TAG_LEVEL))
			{
				String levelKey = aParser.getAttributeValue(null, TAG_LEVEL_ATRB_KEY);
				levelData.put(levelKey, parseLevel(aParser));
			}
			
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_LEVELS);
		return levelData;
	}
	
	private static Level parseLevel(XmlPullParser aParser) throws XmlPullParserException, IOException
	{
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_LEVEL);
		String levelKey = aParser.getAttributeValue(null, TAG_LEVEL_ATRB_KEY);

		String fromWord = null, toWord = null, nextLevel = null, fromImage = null, toImage = null, hint = null;
		int maxMoves = 0;
		boolean isEgg = false;
		ArrayList<String> requiredLevels = null;
		//As long as we are not at an end-tag we will keep creating nodes
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = aParser.getName();
			if (name.equals(LevelDataParser.TAG_FROM_WORD))
			{
				fromWord = readString(aParser, TAG_FROM_WORD);
			}
			else if (name.equals(LevelDataParser.TAG_TO_WORD))
			{
				toWord = readString(aParser, TAG_TO_WORD);
			}
			else if (name.equals(LevelDataParser.TAG_REQUIRED_LEVELS))
			{
				requiredLevels = parseRequiredLevels(aParser);
			}
			else if (name.equals(LevelDataParser.TAG_NEXT_LEVEL))
			{
				nextLevel = readString(aParser, TAG_NEXT_LEVEL);
				
				if (nextLevel != null && nextLevel.equals(""))
				{
					nextLevel = null;
				}
			}
			else if (name.equals(LevelDataParser.TAG_MAX_MOVES))
			{
				String doubleString = readString(aParser, TAG_MAX_MOVES);
				if (doubleString != null && !doubleString.equals(""))
					maxMoves = (int)Double.parseDouble(doubleString);
			}
			else if (name.equals(LevelDataParser.TAG_FROM_IMAGE))
			{
				fromImage = readString(aParser, TAG_FROM_IMAGE);
				
				if (fromImage != null && fromImage.equals(""))
				{
					fromImage = null;
				}
			}
			else if (name.equals(LevelDataParser.TAG_TO_IMAGE))
			{
				toImage = readString(aParser, TAG_TO_IMAGE);
				
				if (toImage != null && toImage.equals(""))
				{
					toImage = null;
				}
			}
			else if (name.equals(LevelDataParser.TAG_HINT))
			{
				hint = readString(aParser, TAG_HINT);
				
				if (hint != null && hint.equals(""))
				{
					hint = null;
				}
			}
			else if (name.equals(LevelDataParser.TAG_EGG))
			{
				String eggString = readString(aParser, TAG_EGG);
				
				if (eggString != null && !eggString.equals(""))
				{
					isEgg = Boolean.parseBoolean(eggString);
				}
			}
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_LEVEL);
		
		return new Level(levelKey, nextLevel, fromWord, toWord, fromImage, toImage, maxMoves, requiredLevels, hint, isEgg);
	}

	private static ArrayList<String> parseRequiredLevels(XmlPullParser aParser) throws XmlPullParserException, IOException 
	{
		ArrayList<String> result = new ArrayList<String>();
		
		//Parses the content of a "node"
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_REQUIRED_LEVELS);
		
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = aParser.getName();
			if (name.equals(LevelDataParser.TAG_REQUIRED_LEVEL))
			{
				String level = readString(aParser, TAG_REQUIRED_LEVEL);
				if (level != null && !level.equals(""))
				{
					result.add(level);
				}
			}
		}
		
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_REQUIRED_LEVELS);
		// Create and return a new MapNode created with the parsed data
		return result;
	}

	private static String readString(XmlPullParser aParser, String aInsideTag)
			throws XmlPullParserException, IOException
	{
		String stringRead = null;
		//Parses the contents of a user defined tag
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, aInsideTag);
		if (aParser.next() == XmlPullParser.TEXT)
		{
			stringRead = aParser.getText();
			aParser.nextTag();
			aParser.require(XmlPullParser.END_TAG, NAMESPACE, aInsideTag);
		}
		
		return stringRead;
	}
}
