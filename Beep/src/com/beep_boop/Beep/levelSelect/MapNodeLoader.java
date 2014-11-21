package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MapNodeLoader
{
	///-----Interfaces-----
	// None
	
	///-----Members-----
	/** XML namespace used in the file. */
	private static final String NAMESPACE = null;
	/** Tag for the node array */
	private static final String TAG_NODES_ARRAY = null;
	/** Tag for an individual node */
	private static final String TAG_NODE = null;
	/** Tag for an individual node’s location X */
	private static final String TAG_NODE_LOCATION_X = null;
	/** Tag for an individual node’s location Y */
	private static final String TAG_NODE_LOCATION_Y = null;
	/** Tag for an individual node’s level KEY*/
	private static final String TAG_NODE_LEVEL_KEY = null;
	
	///-----Constructors-----
	// None
	
	///-----Functions-----
	/** Returns an ArrayList of {@link MapNodes} loaded from an input stream.
	* @param aIn - The input stream to read from
	* @return ArrayList<MapNode> - Array of parsed map nodes
	*/
	public static ArrayList<MapNode> parseFile(InputSteam aIn)
	{
		//@TODO - all
		return null;
	}

	/** Reads all the nodes
	* @param aParser - XML parser to read from
	* @return ArrayList<MapNode> - Array of {@link MapNode}s
	*/
	private static ArrayList<MapNode> parseNodes(XmlPullParser aParser) throws XmlPullParserException, IOException
	{
		//@TODO - all
		return null;
	}
	
	private static MapNode parseNode(XmlPullParser aParser) throws XmlPullParserException, IOException
	{
		//@TODO - all
		return null;
	}
	
	private static float readFloat(XmlPullParser aParser, String aInsideTag) throws XmlPullParserException, IOException
	{
		//@TODO - all
		return 0.0f;
	}
	
	private static String readString(XmlPullParser aParser, String aInsideTag) throws XmlPullParserException, IOException
	{
		//@TODO - all
		return null;
	}
	
	private static String wrapTag(String aInside, boolean aEnd)
	{
		//@TODO - all
		return null;
	}
}
