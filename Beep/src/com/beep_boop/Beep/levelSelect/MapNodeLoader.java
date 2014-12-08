package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

import com.beep_boop.Beep.MyApplication;

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
	/** Tag for an individual nodes level KEY*/
	private static final String TAG_NODE_LEVEL_KEY = "levelKey";
	private static final String TAG_OFF_IMAGE = "offImage";
	private static final String TAG_ON_IMAGE = "onImage";
	
	private static final String PACKAGE = "com.beep_boop.Beep";
	private static final String TYPE = "drawable";

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
	public static ArrayList<MapNode> parseFile(InputStream aIn) 
	{
		ArrayList<MapNode> results = null;
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
			results = parseNodes(aParser);
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

	/**
	 * Reads all the nodes
	 * 
	 * @param aParser
	 *            - XML parser to read from
	 * @return ArrayList<MapNode> - Array of {@link MapNode}s
	 */

	private static ArrayList<MapNode> parseNodes(XmlPullParser aParser) throws XmlPullParserException, IOException 
	{
		//Parses the content of a "nodes"
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_NODES_ARRAY);
		ArrayList<MapNode> nodeList = new ArrayList<MapNode>();
		//As long as we are not at an end-tag we will keep creating nodes
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = aParser.getName();
			if (name.equals(TAG_NODE))
			{
				// Call the parseNode function to create a new MapNode from each
				// segment of the data
				MapNode mNode = parseNode(aParser);
				nodeList.add(mNode);
			}
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_NODES_ARRAY);
		return nodeList;
	}

	private static MapNode parseNode(XmlPullParser aParser) throws XmlPullParserException, IOException 
	{
		float xVal = 0f, yVal = 0f;
		String levelKey = null, offImageName = null, onImageName = null;
		//Parses the content of a "node"
		aParser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_NODE);
		while (aParser.next() != XmlPullParser.END_TAG)
		{
			//If we are in blank space, keep advancing the parser until we hit a start tag
			if (aParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = aParser.getName();
			if (name.equals(TAG_NODE_LOCATION_X))
			{
				xVal = readFloat(aParser, TAG_NODE_LOCATION_X);
			}
			else if (name.equals(TAG_NODE_LOCATION_Y))
			{
				yVal = readFloat(aParser, TAG_NODE_LOCATION_Y);
			}
			else if (name.equals(TAG_NODE_LEVEL_KEY))
			{
				levelKey = readString(aParser, TAG_NODE_LEVEL_KEY);
			}
			else if (name.equals(TAG_OFF_IMAGE))
			{
				offImageName = readString(aParser, TAG_OFF_IMAGE);
				if (offImageName != null && offImageName.equals(""))
				{
					offImageName = null;
				}
			}
			else if (name.equals(TAG_ON_IMAGE))
			{
				onImageName = readString(aParser, TAG_ON_IMAGE);
				if (onImageName != null && onImageName.equals(""))
				{
					onImageName = null;
				}
			}
		}
		aParser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_NODE);

		MapNode result = new MapNode(xVal, yVal, levelKey);

		if (offImageName != null)
		{
			Bitmap offImage = null;
			int imageId = MyApplication.getAppContext().getResources().getIdentifier(offImageName, TYPE, PACKAGE);
			if (imageId != -1)
				offImage = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), imageId, null);
			result.offIcon = offImage;
		}
		if (onImageName != null)
		{
			Bitmap onImage = null;
			int imageId = MyApplication.getAppContext().getResources().getIdentifier(onImageName, TYPE, PACKAGE);
			if (imageId != -1)
				onImage = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), imageId, null);
			result.onIcon = onImage;
		}
		// Create and return a new MapNode created with the parsed data
		return result;
	}

	private static float readFloat(XmlPullParser aParser, String aInsideTag) throws XmlPullParserException, IOException
	{
		float floatVal = 0.0f;
		//Parses the contents of a user defined tag
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

	private static String readString(XmlPullParser aParser, String aInsideTag) throws XmlPullParserException, IOException
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
