package com.beep_boop.Beep.win;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.beep_boop.Beep.R;
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_win);

		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(WinActivity.EXTRA_LEVEL_KEY) && 
					extras.containsKey(WinActivity.EXTRA_TIME) && 
					extras.containsKey(WinActivity.EXTRA_PATH))
			{
				String levelKey = extras.getString(WinActivity.EXTRA_LEVEL_KEY);
				double time = extras.getDouble(WinActivity.EXTRA_TIME);
				this.mPath = extras.getStringArray(WinActivity.EXTRA_PATH);
				LevelManager.setLevelComplete(levelKey, true, time, this.mPath.length - 1);
				this.mCompletedLevel = LevelManager.getLevelForKey(levelKey);
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
	}
}
