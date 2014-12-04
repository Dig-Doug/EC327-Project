package com.beep_boop.Beep.win;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class WinActivity extends Activity
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	public static final String EXTRA_PATH = "EXTRA_PATH";
	public static final String EXTRA_TIME = "EXTRA_TIME";
	/** Tag for logging */
	private static final String TAG = "WinActivity";
	private WinActivity THIS = this;
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
			if (extras.containsKey(WinActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(WinActivity.EXTRA_LEVEL_KEY);
				this.mCompletedLevel = LevelManager.getLevelForKey(levelKey);

				if (extras.containsKey(WinActivity.EXTRA_TIME) && extras.containsKey(WinActivity.EXTRA_PATH))
				{
					time = extras.getDouble(WinActivity.EXTRA_TIME);
					this.mPath = extras.getStringArray(WinActivity.EXTRA_PATH);

					if (time < this.mCompletedLevel.time)
						personalBestTime = true;
					if (this.mPath.length - 1 < this.mCompletedLevel.numberOfSteps)
						personalBestMoves = true;

					LevelManager.setLevelComplete(levelKey, true, time, this.mPath.length - 1);
				}
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
		
		Typeface customFont = Typeface.createFromAsset(getAssets(), MyApplication.FONT);
		
		TextView title = (TextView) findViewById(R.id.winActivity_titleLabel);
		title.setTypeface(customFont);
		
		TextView subTitle = (TextView) findViewById(R.id.winActivity_successLabel);
		subTitle.setTypeface(customFont);

		this.mTimePlaceholderLabel = (TextView) findViewById(R.id.winActivity_timeLabel);
		this.mTimePlaceholderLabel.setText(getString(R.string.winActivity_timeLabel) + " " + (int)(time / 1000) + " " + getString(R.string.winActivity_timeSuffix));
		this.mTimePlaceholderLabel.setTypeface(customFont);
		
		this.mMovePlaceholderLabel = (TextView) findViewById(R.id.winActivity_moveLabel);
		this.mMovePlaceholderLabel.setText(getString(R.string.winActivity_moveLabel) + " " + (this.mPath.length - 1) + " " + getString(R.string.winActivity_moveSuffix));
		this.mMovePlaceholderLabel.setTypeface(customFont);
		
		this.setupButtons();
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
		String message = getString(R.string.share_emailDefaultText1) + this.mCompletedLevel.fromWord + 
				getString(R.string.share_emailDefaultText2) + this.mCompletedLevel.toWord + 
				getString(R.string.share_emailDefaultText3) + this.mCompletedLevel.numberOfSteps + 
				getString(R.string.share_emailDefaultText4) + this.mCompletedLevel.time + 
				getString(R.string.share_emailDefaultText5);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(intent, "Send Email"));
	}
}
