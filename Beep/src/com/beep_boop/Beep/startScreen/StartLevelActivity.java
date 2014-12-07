package com.beep_boop.Beep.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
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
		MyApplication.activityStarted(this);
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

		TextView title = (TextView) findViewById(R.id.startScreenActivity_titleTextView);
		title.setTypeface(MyApplication.MAIN_FONT);

		TextView move = (TextView) findViewById(R.id.startScreenActivity_movesTextView);
		move.setTypeface(MyApplication.MAIN_FONT);
		move.setText(getString(R.string.startLevelActivity_moves) + " " + this.mSelectedLevel.maxMoves);

		TextView best = (TextView) findViewById(R.id.startScreenActivity_bestTextView);
		best.setTypeface(MyApplication.MAIN_FONT);
		best.setText(getString(R.string.startLevelActivity_best) + " " + (this.mSelectedLevel.numberOfSteps == Integer.MAX_VALUE ? "X" : this.mSelectedLevel.numberOfSteps));

		Bitmap fromBit = null, toBit = null;
		try
		{
			fromBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.fromImage));
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error getting from level image");
		}
		try
		{
			toBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.toImage));
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error getting to level image");
		}
		this.mWordDisplay = (WordDisplay) findViewById(R.id.startScreenActivity_wordDisplay);
		this.mWordDisplay.set(fromBit, toBit, this.mSelectedLevel.fromWord, this.mSelectedLevel.toWord);

		ImageButton playButton = (ImageButton) findViewById(R.id.startLevelActivity_startLevelButton);
		playButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent playIntent = new Intent(THIS, PlayScreenActivity.class);
				playIntent.putExtra(PlayScreenActivity.EXTRA_LEVEL_KEY, mSelectedLevel.levelKey);
				startActivity(playIntent);
				overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
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
	
	@Override
	protected void onRestart(){
		super.onRestart();
		MyApplication.mServ.resumeMusic();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		MyApplication.activityPaused(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.mWordDisplay.destroy();
	}
}
