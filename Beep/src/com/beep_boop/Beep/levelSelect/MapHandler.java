package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.beep_boop.Beep.R;

public class MapHandler
{
	///-----Static Variables-----
	/** Holds if the singleton has been loaded */
	private static boolean loaded = false, startedLoading = false;
	/** Holds the log tag */
	private static final String TAG = "MapHandler";
	/** Holds the singleton instance of the class */
	public static MapHandler INSTANCE = new MapHandler();

	///-----Member Variables-----
	private ArrayList<MapNode> mNodes;

	///-----Constructors-----	
	private MapHandler()
	{

	}

	///-----Public Wrapper Methods-----
	public static void load(Context aContext)
	{
		if (!MapHandler.loaded && !MapHandler.startedLoading)
		{
			MapHandler.startedLoading = true;
			MapHandler.INSTANCE = new MapHandler();
			MapHandler.INSTANCE.loadPrivate(aContext);
			MapHandler.loaded = true;
		}
	}
	
	public static boolean getLoaded()
	{
		return MapHandler.loaded;
	}

	public static ArrayList<MapNode> getNodes()
	{
		return MapHandler.INSTANCE.getNodesPrivate();
	}

	///-----Private Methods-----
	private void loadPrivate(Context aContext)
	{
		//load the nodes
		InputStream in = null;
		try 
		{
			in = aContext.getResources().openRawResource(R.raw.map_data_file);
			this.mNodes = MapNodeLoader.parseFile(in);
		}
		catch (Exception i)
		{
			Log.e(MapHandler.TAG, "The IOException was caught.");
		}
		finally 
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{

				}
			}
		}
	}

	public ArrayList<MapNode> getNodesPrivate()
	{
		return this.mNodes;
	}

}
