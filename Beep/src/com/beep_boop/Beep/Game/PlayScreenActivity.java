package com.beep_boop.Beep.Game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.beep_boop.Beep.MainActivity;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PlayScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_screen);
		
		Hashtable<String, Hashtable<String, Integer>> wikiData = null; 
		
		InputStream in = null;
		try 
		{
			in = getResources().openRawResource(R.raw.matthew_mcconaughey_zurich);
			
			wikiData = PlayScreenParser.parseFile(in);
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