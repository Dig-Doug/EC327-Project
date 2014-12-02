package com.beep_boop.Beep.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

		Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Krungthep.ttf");

		TextView title = (TextView) findViewById(R.id.startScreenActivity_titleTextView);
		title.setTypeface(customFont);

		TextView move = (TextView) findViewById(R.id.startScreenActivity_movesTextView);
		move.setTypeface(customFont);
		move.setText(getString(R.string.startLevelActivity_moves) + this.mSelectedLevel.maxMoves);

		TextView best = (TextView) findViewById(R.id.startScreenActivity_bestTextView);
		best.setTypeface(customFont);
		best.setText(getString(R.string.startLevelActivity_best) + (this.mSelectedLevel.numberOfSteps == Double.MAX_VALUE ? "X" : this.mSelectedLevel.numberOfSteps));

		try
		{
			Bitmap fromBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.fromImage));
			Bitmap toBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.toImage));
			this.mWordDisplay = (WordDisplay) findViewById(R.id.startScreenActivity_wordDisplay);
			this.mWordDisplay.set(fromBit, toBit, this.mSelectedLevel.fromWord, this.mSelectedLevel.toWord);
		}
		catch (Exception e)
		{

		}

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
