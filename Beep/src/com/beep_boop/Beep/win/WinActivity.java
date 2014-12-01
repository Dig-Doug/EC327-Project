package com.beep_boop.Beep.win;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenActivity;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;

public class WinActivity extends Activity
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	public static final String EXTRA_PATH = "EXTRA_PATH";
	public static final String EXTRA_TIME = "EXTRA_TIME";
	/** Tag for logging */
	private static final String TAG = "WinActivity";
	private Level mCompletedLevel;
	private String[] mPath;

	private TextView mTimePlaceholderLabel;
	private TextView mMovePlaceholderLabel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_win);

		Bundle extras = this.getIntent().getExtras();
		double time = 0;
		boolean personalBestTime = false, personalBestMoves = false;
		if (extras != null)
		{
			if (extras.containsKey(WinActivity.EXTRA_LEVEL_KEY) && 
					extras.containsKey(WinActivity.EXTRA_TIME) && 
					extras.containsKey(WinActivity.EXTRA_PATH))
			{
				String levelKey = extras.getString(WinActivity.EXTRA_LEVEL_KEY);
				this.mCompletedLevel = LevelManager.getLevelForKey(levelKey);
				
				time = extras.getDouble(WinActivity.EXTRA_TIME);
				this.mPath = extras.getStringArray(WinActivity.EXTRA_PATH);
				
				if (time < this.mCompletedLevel.time)
					personalBestTime = true;
				if (this.mPath.length - 1 < this.mCompletedLevel.numberOfSteps)
					personalBestMoves = true;
				
				LevelManager.setLevelComplete(levelKey, true, time, this.mPath.length - 1);
			}
			else
			{
				Log.w(WinActivity.TAG, "Missing bundle item, level progress will not be stored");
			}
		}
		else
		{
			Log.e(WinActivity.TAG, "Error getting extras");
			finish();
		}
		
		this.mTimePlaceholderLabel = (TextView) findViewById(R.id.winActivity_timePlaceholderLabel);
		this.mMovePlaceholderLabel = (TextView) findViewById(R.id.winActivity_movePlaceholderLabel);
		
		this.mTimePlaceholderLabel.setText((int)(time / 1000) + " " + getString(R.string.winActivity_timeSuffix));
		this.mMovePlaceholderLabel.setText((this.mPath.length - 1) + " " + getString(R.string.winActivity_moveSuffix));
		
		ImageButton mapButton = (ImageButton) findViewById(R.id.winActivity_mapButton);
		mapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
