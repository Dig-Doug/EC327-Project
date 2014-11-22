package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.beep_boop.Beep.R;

public class MapActivity extends Activity 
{

	private MapView mView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mView = (MapView)findViewById(R.id.mapActivity_mapView);
		
		ArrayList<MapNode> nodeList = null;
		InputStream in = null;
		try 
		{
			in = getResources().openRawResource(R.raw.nodes_test_file);
			
			nodeList = MapNodeLoader.parseFile(in);
		}
		catch (Exception i)
		{
			Log.e("fileinput", "The IOException was caught.");
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
		mView.addNodes(nodeList); 
	}


}
