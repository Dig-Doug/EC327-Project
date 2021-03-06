package com.beep_boop.Beep.win;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.beep_boop.Beep.statistics.StatisticsManager;

public class WinActivity extends Activity
{
	///-----Member Variables-----
	private WinActivity THIS = this;
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	public static final String EXTRA_PATH = "EXTRA_PATH";
	public static final String EXTRA_TIME = "EXTRA_TIME";
	public static final String EXTRA_FROM_WORD = "EXTRA_FROM_WORD";
	public static final String EXTRA_TO_WORD = "EXTRA_TO_WORD";
	/** Tag for logging */
	private static final String TAG = "WinActivity";
	//private WinActivity THIS = this;
	private Level mCompletedLevel;
	private String[] mPath;
	private double mTime = 0;
	private String mFromWord, mToWord;

	private TextView mTimePlaceholderLabel;
	private TextView mMovePlaceholderLabel;

	private StarryBackgroundView mStarBackground;

	boolean activityStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_win);
		MyApplication.playSong();

		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.winActivity_background);

		Bundle extras = this.getIntent().getExtras();
		//boolean personalBestTime = false, personalBestMoves = false;
		if (extras != null)
		{
			if (extras.containsKey(WinActivity.EXTRA_TIME) && extras.containsKey(WinActivity.EXTRA_PATH))
			{
				this.mTime = extras.getDouble(WinActivity.EXTRA_TIME);
				this.mPath = extras.getStringArray(WinActivity.EXTRA_PATH);
			}

			if (extras.containsKey(WinActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(WinActivity.EXTRA_LEVEL_KEY);
				this.mCompletedLevel = LevelManager.getLevelForKey(levelKey);
				this.mFromWord = this.mCompletedLevel.fromWord;
				this.mToWord = this.mCompletedLevel.toWord;
				LevelManager.setLevelComplete(levelKey, true, this.mTime, this.mPath.length - 1);
				StatisticsManager.recordLevel(this.mCompletedLevel, this.mPath);
			}
			else
			{
				this.mFromWord = extras.getString(WinActivity.EXTRA_FROM_WORD);
				this.mToWord = extras.getString(WinActivity.EXTRA_TO_WORD);
				this.mStarBackground.setBackgroundImage(R.drawable.random_background);
				StatisticsManager.recordRandom(this.mFromWord, this.mToWord, this.mPath, this.mTime);
			}
		}
		else
		{
			Log.e(WinActivity.TAG, "Error getting extras");
			activityStarted = true;
			finish();
		}

		this.mTimePlaceholderLabel = (TextView) findViewById(R.id.winActivity_timeLabel);
		this.mTimePlaceholderLabel.setText(getString(R.string.winActivity_timeLabel) + " " + (int)(this.mTime / 1000) + " " + getString(R.string.winActivity_timeSuffix));
		this.mTimePlaceholderLabel.setTypeface(MyApplication.MAIN_FONT);

		this.mMovePlaceholderLabel = (TextView) findViewById(R.id.winActivity_moveLabel);
		this.mMovePlaceholderLabel.setText(getString(R.string.winActivity_moveLabel) + " " + (this.mPath.length - 1) + " " + getString(R.string.winActivity_moveSuffix));
		this.mMovePlaceholderLabel.setTypeface(MyApplication.MAIN_FONT);

		this.setupButtons();


		SharedPreferences sharedPref = getSharedPreferences("MAIN", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(getString(R.string.prefs_levelsPlayedCount), sharedPref.getInt(getString(R.string.prefs_levelsPlayedCount), 0) + 1);
		editor.commit();
	}

	private void setupButtons()
	{
		/*
		ImageButton nextLevelButton = (ImageButton) findViewById(R.id.winActivity_nextLevelButton);
		nextLevelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent startLevelIntent = new Intent(THIS, StartLevelActivity.class);
				startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mCompletedLevel.nextLevelKey);
				startActivity(startLevelIntent);
				finish();
			}
		});

		if (this.mCompletedLevel.nextLevelKey == null || !LevelManager.canPlayLevel(this.mCompletedLevel.nextLevelKey))
		{
			nextLevelButton.setAlpha(0.0f);
			nextLevelButton.setEnabled(false);
		}
		 */

		ImageButton mapButton = (ImageButton) findViewById(R.id.winActivity_mapButton);
		mapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent pathIntent = new Intent(THIS, PathActivity.class);
				if (mCompletedLevel != null)
					pathIntent.putExtra(WinActivity.EXTRA_LEVEL_KEY, mCompletedLevel.levelKey);
				pathIntent.putExtra(WinActivity.EXTRA_PATH, mPath);
				startActivity(pathIntent);
				activityStarted = true;
				overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
				finish();
			}
		});

		ImageButton shareButton = (ImageButton) findViewById(R.id.winActivity_shareButton);
		shareButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareWithFriend();
			}
		});
	}

	private void shareWithFriend()
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse("mailto:?subject=" + "" + "&body=" + "" + "&to=" + "");  
		intent.setData(data);  
		String message = getString(R.string.share_emailDefaultText1) + " " + this.mFromWord + " " +
				getString(R.string.share_emailDefaultText2) + " " + this.mToWord + " " +
				getString(R.string.share_emailDefaultText3) + " " + (this.mPath.length - 1) + " " + 
				getString(R.string.share_emailDefaultText4) + " " + (this.mTime / 1000) + " " + 
				getString(R.string.share_emailDefaultText5);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(intent, "Send Email"));
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		MyApplication.playSong();

	}
	@Override
	protected void onResume(){
		super.onResume();
		MyApplication.playSong();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//MyApplication.pauseSong();

		if (this.mStarBackground != null)
		{
			this.mStarBackground.destroy();
		}
	}
	@Override
	public void onBackPressed(){
		activityStarted = true;
		finish();

	}
}
