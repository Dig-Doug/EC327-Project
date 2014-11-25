package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class LevelRequirementParser
{
	private static final String TAG = "LevelRequirementParser";
	/** XML namespace used in the file. */
	private static final String NAMESPACE = null;

	private static final String TAG_LEVELS = "levels";
	private static final String TAG_LEVEL = "level";
	private static final String TAG_LEVEL_ATRB_KEY = "key";
	private static final String TAG_REQUIRED_LEVELS = "requires";
	private static final String TAG_REQUIRED_LEVEL = "req";

	///-----Constructors-----
	// None

	///-----Functions-----
	public static HashMap<String, String[]> parseFile(InputStream aIn) 
	{
		HashMap<String, String[]> results = null;
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
		} 
		catch (IOException i) 
		{
			Log.e(TAG, "The IOException was caught.");
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

	private static HashMap<String, String[]> parseLevels(XmlPullParser aParser) throws XmlPullParserException, IOException 
	{
		//Parses the content of a "nodes"
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_LEVELS);
		HashMap<String, String[]> levelData = new HashMap<String, String[]>();
		//As long as we are not at an end-tag we will keep creating nodes
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = aParser.getName();
			if (name.equals(LevelRequirementParser.TAG_LEVEL))
			{
				String levelKey = aParser.getAttributeValue(null, TAG_LEVEL_ATRB_KEY);
				aParser.nextTag();
				if (aParser.getName().equals(LevelRequirementParser.TAG_REQUIRED_LEVELS))
				{
					String[] requiredLevels = parseRequiredLevels(aParser);
					levelData.put(levelKey, requiredLevels);
				}
				else
				{
					levelData.put(levelKey, null);
				}
			}
			
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_LEVELS);
		return levelData;
	}

	private static String[] parseRequiredLevels(XmlPullParser aParser) throws XmlPullParserException, IOException 
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
			if (name.equals(LevelRequirementParser.TAG_REQUIRED_LEVEL))
			{
				result.add(readString(aParser, TAG_REQUIRED_LEVEL));
			}
		}
		
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_REQUIRED_LEVELS);
		aParser.nextTag();
		// Create and return a new MapNode created with the parsed data
		return (String[])result.toArray();
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
		aParser.nextTag();
		
		return stringRead;
	}
}