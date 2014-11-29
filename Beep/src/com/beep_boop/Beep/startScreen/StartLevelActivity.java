package com.beep_boop.Beep.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenActivity;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;

public class StartLevelActivity extends Activity
{	
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	
	private static final String TAG = "StartLevelActivity";
	private StartLevelActivity THIS = this;
	private TextView mStartWordView;
	private TextView mEndWordView;
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
		
		this.mStartWordView = (TextView) findViewById(R.id.startLevelActivity_startWordTextView);
		this.mEndWordView = (TextView) findViewById(R.id.startLevelActivity_toWordTextView);
		this.mStartWordView.setText(this.mSelectedLevel.fromWord);
		this.mEndWordView.setText(this.mSelectedLevel.toWord);
		
		Button playButton = (Button) findViewById(R.id.startLevelStartButton);
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
		
		Button toMapButton = (Button) findViewById(R.id.startLevelBackButton);
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
