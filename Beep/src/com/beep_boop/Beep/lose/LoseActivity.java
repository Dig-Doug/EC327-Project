package com.beep_boop.Beep.lose;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.stars.StarryBackgroundView;
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class LoseActivity extends Activity
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	/** Tag for logging */
	private static final String TAG = "LoseActivity";
	private LoseActivity THIS = this;
	private Level mLevel;
	
	private StarryBackgroundView mStarBackground;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lose);
		
		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.aboutActivity_background);
		
		MyApplication.activityStarted(this);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(LoseActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(LoseActivity.EXTRA_LEVEL_KEY);
				this.mLevel = LevelManager.getLevelForKey(levelKey);
			}
			else
			{
				Log.w(LoseActivity.TAG, "Missing bundle item, level progress will not be stored");
			}
		}
		else
		{
			Log.e(LoseActivity.TAG, "Error getting extras");
			finish();
		}
		
		this.setupButtons();
		
		TextView hintView = (TextView) findViewById(R.id.loseActivity_hintText);
		hintView.setText(getString(R.string.loseActivity_hint) + " " + this.mLevel.hint);
		hintView.setTypeface(MyApplication.MAIN_FONT);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		MyApplication.activityPaused(this);
	}
	
	private void setupButtons()
	{
		ImageButton restartLevelButton = (ImageButton) findViewById(R.id.loseActivity_restartLevelButton);
		restartLevelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent startLevelIntent = new Intent(THIS, StartLevelActivity.class);
				startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mLevel.levelKey);
				startActivity(startLevelIntent);
				finish();
			}
		});
		
		ImageButton mapButton = (ImageButton) findViewById(R.id.loseActivity_mapButton);
		mapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.mStarBackground.destroy();
	}
}
