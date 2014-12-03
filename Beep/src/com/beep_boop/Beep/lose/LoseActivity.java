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
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class LoseActivity extends Activity
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	/** Tag for logging */
	private static final String TAG = "LoseActivity";
	private LoseActivity THIS = this;
	private Level mLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lose);

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
		
		Typeface customFont = Typeface.createFromAsset(getAssets(), MyApplication.FONT);
		
		TextView title = (TextView) findViewById(R.id.loseActivity_titleLabel);
		title.setTypeface(customFont);
		
		TextView subTitle = (TextView) findViewById(R.id.loseActivity_subtitleLabel);
		subTitle.setTypeface(customFont);
		
		this.setupButtons();
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
				startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mLevel.nextLevelKey);
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
}
