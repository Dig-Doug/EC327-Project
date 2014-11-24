package com.beep_boop.Beep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.beep_boop.Beep.game.PlayScreenActivity;
import com.beep_boop.Beep.launch.LaunchActivity;
import com.beep_boop.Beep.levelSelect.MapActivity;
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class MainActivity extends Activity
{
	/** Tag used in Log messages */
	private static final String TAG = "MainActivity";
	/** Reference to this */
	private Activity THIS = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button toSettingsButton = (Button) findViewById(R.id.mainActivity_toSettingsButton);
		toSettingsButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MainActivity.TAG, "To settings button clicked");
				
				Intent toSettings = new Intent(THIS, AboutActivity.class);
				startActivity(toSettings);
			}
		});
		
		Button toLaunchButton = (Button) findViewById(R.id.mainActivity_toLaunchButton);
		toLaunchButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MainActivity.TAG, "To launch button clicked");
				
				Intent toLaunch = new Intent(THIS, LaunchActivity.class);
				startActivity(toLaunch);
			}
		});
		
		Button toMapButton = (Button) findViewById(R.id.mainActivity_toMapButton);
		toMapButton.setOnClickListener(new OnClickListener()
		{
			@Override 
			public void onClick(View v)
			{
				Log.d(MainActivity.TAG, "To map button clicked");
				
				Intent toMap = new Intent(THIS, MapActivity.class);
				startActivity(toMap); 
				
			}
				
		});
		
		Button toPlayScreenButton = (Button) findViewById(R.id.mainActivity_toPlayButton);
		toPlayScreenButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MainActivity.TAG, "To play screen button clicked");
				
				Intent toPlayscreen = new Intent(THIS,PlayScreenActivity.class);
				startActivity(toPlayscreen); 
				
			}
		});
		
		Button toStartButton = (Button) findViewById(R.id.mainActivity_toStartButton);
		toStartButton.setOnClickListener(new OnClickListener()
		{
			@Override 
			public void onClick(View v)
			{
				Log.d(MainActivity.TAG, "To start button clicked");
				
				Intent toStart = new Intent(THIS, StartLevelActivity.class);
				startActivity(toStart); 
				
			}
				
		});
		
	}
}
