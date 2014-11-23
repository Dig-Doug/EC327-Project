package com.beep_boop.Beep.Game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class PlayScreenParser {

	///-----Members-----
	private static final String TAG = "playScreenParser"; 
	private static final String NAMESPACE = null;
	private static final String TAG_WORD = "word";
	private static final String TAG_WORD_ATRB_TITLE = "title";
	//private static final String TAG_LINKCOUNT = "linkCount"; 
	private static final String TAG_LINKS = "links";
	private static final String TAG_LINK = "link"; 
	private static final String TAG_LINK_ATRB_COUNT = "count"; 

	public static Hashtable<String, Hashtable<String, Integer>> parseFile(InputStream aIn)
	{
		Hashtable<String, Hashtable<String, Integer>> wikiData = null; 
		try 
		{
			//Create a parser
			XmlPullParser aParser = Xml.newPullParser();
			aParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//We set the input to our parser as the input file
			aParser.setInput(aIn, null);
			//Advance our parser to the next tag
			aParser.nextTag();
			//Call the parseLinks function to obtain a Hashtable of link titles and their counts
			wikiData = parseWords(aParser);
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

		return wikiData;
	}

	private static Hashtable<String, Hashtable<String, Integer>> parseWords(XmlPullParser aParser) throws XmlPullParserException, IOException
	{
		Hashtable<String, Hashtable<String, Integer>> wikiData = new Hashtable<String, Hashtable<String, Integer>>();
		
		while (aParser.next() != XmlPullParser.END_TAG) 
		{
			if (aParser.getEventType() != XmlPullParser.START_TAG) 
			{
				continue;
			}
			
			String name = aParser.getName();
			if (name.equals(TAG_WORD)) 
			{
				String pageName = aParser.getAttributeValue(null, TAG_WORD_ATRB_TITLE);
				Hashtable<String, Integer> links = null;
				while (true) 
				{
					aParser.next();
					if (aParser.getEventType() != XmlPullParser.START_TAG) 
					{
						continue;
					}
					
					String nameOfTag = aParser.getName();
					if (nameOfTag.equals(TAG_LINKS)) 
					{
						links = readLinks(aParser);
						break;
					}
				}
				
				wikiData.put(pageName, links);
			}
			else 
			{
				Log.e(TAG, "Tag contained something I did not specify.");
				//skip(aParser);
			}     
		}

		return wikiData;
	}

	//Reads into Links, and creates a HashTable with all the Links and their counts 
	private static Hashtable<String, Integer> readLinks(XmlPullParser aParser) throws XmlPullParserException, IOException
	{
		Hashtable<String, Integer> linkData = new Hashtable<String, Integer>();
		
		//make sure we have a <links> tag
		//this require threw an error
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_LINKS);
		aParser.nextTag();
		
		//This while loop will run until we hit a </links> end tag
		while(aParser.getEventType() != XmlPullParser.END_TAG)
		{
			if (aParser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			String name = aParser.getName(); 
			if(name.equals(TAG_LINK))
			{
				int count = (int)Double.parseDouble(aParser.getAttributeValue(null, TAG_LINK_ATRB_COUNT));
				String link = null;
				if (aParser.next() == XmlPullParser.TEXT)
				{
					link = aParser.getText();
				}
				aParser.nextTag();
				aParser.nextTag();

				//save it in hash table
				linkData.put(link, count);
			}
			else
			{
				Log.e(PlayScreenParser.TAG, "Unknown tag name: " + name);
			}
		}
		
		//Tell the parser to require a 
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_LINKS);
		//go past the end </links>
		aParser.nextTag();

		return linkData; 
	}
	
	/*
	private static int readTotalLinks(XmlPullParser aParser) throws XmlPullParserException, IOException
	{

		int totalLinks = 0;
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_LINKCOUNT);
		if (aParser.next() == XmlPullParser.TEXT)
		{
			//Convert the total links from a string to an int and store it 
			totalLinks = (int)Double.parseDouble(aParser.getText()); 
			aParser.nextTag();
			aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_LINKCOUNT);
		}

		return totalLinks;
	}
	*/
}
