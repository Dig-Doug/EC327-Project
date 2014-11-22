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

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		ArrayList<MapNode> nodeList = null;
		InputStream in = null;
		try 
		{
			in = getResources().openRawResource(R.id.aboutActivity_messageEditText);
			
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
	}


}
