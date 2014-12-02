package com.beep_boop.Beep.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenActivity;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;

public class StartLevelActivity extends Activity
{	
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	
	private static final String TAG = "StartLevelActivity";
	private StartLevelActivity THIS = this;
	private WordDisplay mWordDisplay;
	private Level mSelectedLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_level);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(StartLevelActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(StartLevelActivity.EXTRA_LEVEL_KEY);
				this.mSelectedLevel = LevelManager.getLevelForKey(levelKey);
			}
		}
		else
		{
			Log.e(StartLevelActivity.TAG, "Error getting extras");
			finish();
		}
		
		Drawable fromImage = getResources().getDrawable(R.drawable.ss_apple);
		Bitmap fromBit = ((BitmapDrawable) fromImage).getBitmap();
		Drawable toImage = getResources().getDrawable(R.drawable.ss_viking);
		Bitmap toBit = ((BitmapDrawable) toImage).getBitmap();
		this.mWordDisplay = (WordDisplay) findViewById(R.id.startScreenActivity_wordDisplay);
		this.mWordDisplay.set(fromBit, toBit, "Test", "To");
		
		ImageButton playButton = (ImageButton) findViewById(R.id.startLevelActivity_startLevelButton);
		playButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent playIntent = new Intent(THIS, PlayScreenActivity.class);
				playIntent.putExtra(PlayScreenActivity.EXTRA_LEVEL_KEY, mSelectedLevel.levelKey);
				startActivity(playIntent);
				finish();
			}
		});
		
		ImageButton toMapButton = (ImageButton) findViewById(R.id.startLevelActivity_backToMapButton);
		toMapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(StartLevelActivity.TAG, "To map button clicked");
				
				finish();
			}
		});
		
	}
}
