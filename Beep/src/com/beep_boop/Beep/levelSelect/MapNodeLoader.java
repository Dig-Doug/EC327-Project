package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class MapNodeLoader {
	// /-----Interfaces-----
	// None

	// /-----Members-----
	private static final String TAG = "mapNodeLoader";

	/** XML namespace used in the file. */
	private static final String NAMESPACE = null;
	/** Tag for the node array */
	private static final String TAG_NODES_ARRAY = "nodes";
	/** Tag for an individual node */
	private static final String TAG_NODE = "node";
	/** Tag for an individual nodes location X */
	private static final String TAG_NODE_LOCATION_X = "locX";
	/** Tag for an individual nodes location Y */
	private static final String TAG_NODE_LOCATION_Y = "locY";
<<<<<<< Updated upstream
	/** Tag for an individual node�s level KEY */
=======
	/** Tag for an individual nodes level KEY*/
>>>>>>> Stashed changes
	private static final String TAG_NODE_LEVEL_KEY = "levelKey";

	// /-----Constructors-----
	// None

	// /-----Functions-----
	/**
	 * Returns an ArrayList of {@link MapNodes} loaded from an input stream.
	 * 
	 * @param aIn
	 *            - The input stream to read from
	 * @return ArrayList<MapNode> - Array of parsed map nodes
	 */
	public static ArrayList<MapNode> parseFile(InputStream aIn) {
		ArrayList<MapNode> results = null;
		try {
			XmlPullParser aParser = Xml.newPullParser();
			aParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			aParser.setInput(aIn, null);
			aParser.nextTag();
			results = parseNodes(aParser);
		} catch (XmlPullParserException e) {
			Log.e(TAG, "The XmlPullParserException was caught.");
		} catch (IOException i) {
			Log.e(TAG, "The IOException was caught.");
		} finally {
			try {
				aIn.close();
			} catch (IOException i) {
				Log.e(TAG, "The IOException was caught from the aIn.close().");
			}
		}

		return results;
	}

	/**
	 * Reads all the nodes
	 * 
	 * @param aParser
	 *            - XML parser to read from
	 * @return ArrayList<MapNode> - Array of {@link MapNode}s
	 */

	private static ArrayList<MapNode> parseNodes(XmlPullParser aParser)
			throws XmlPullParserException, IOException {
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_NODES_ARRAY);
		ArrayList<MapNode> nodeList = new ArrayList<MapNode>();
		// Create a new node until we hit the </nodes> end tag
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			// Call the parseNode function to create a new MapNode from each
			// segment of the data
			MapNode mNode = parseNode(aParser);
			// Add the newly created MapNode into our array of MapNodes
			nodeList.add(mNode);
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_NODES_ARRAY);
		return nodeList;
	}

	private static MapNode parseNode(XmlPullParser aParser)
			throws XmlPullParserException, IOException {

		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_NODE);
		aParser.nextTag();
		float xVal = readFloat(aParser, TAG_NODE_LOCATION_X);
		aParser.nextTag();
		float yVal = readFloat(aParser, TAG_NODE_LOCATION_Y);
		aParser.nextTag();
		String levelKey = readString(aParser, TAG_NODE_LEVEL_KEY);
		aParser.nextTag();
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_NODE);
		// Create and return a new MapNode created with the parsed data
		return new MapNode(xVal, yVal, levelKey);
	}

	private static float readFloat(XmlPullParser aParser, String aInsideTag)
			throws XmlPullParserException, IOException
			{
		float floatVal = 0.0f;
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, aInsideTag);
		if (aParser.next() == XmlPullParser.TEXT)
		{
			String val = aParser.getText();
			if (val != null)
				floatVal = (float)Double.parseDouble(val);
			aParser.nextTag();
			aParser.require(XmlPullParser.END_TAG, NAMESPACE, aInsideTag);
		}

		return floatVal;
			}

	private static String readString(XmlPullParser aParser, String aInsideTag)
			throws XmlPullParserException, IOException
			{
		String stringRead = null;
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
