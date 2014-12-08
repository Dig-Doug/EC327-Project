package com.beep_boop.Beep.random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenActivity;
import com.beep_boop.Beep.game.WordHandler;
import com.beep_boop.Beep.stars.StarryBackgroundView;
import com.beep_boop.Beep.startScreen.WordDisplay;

public class RandomActivity extends Activity
{
	private static final String TAG = "RandomActivity";
	public static final String EXTRA_FROM_WORD = "EXTRA_FROM_WORD";
	public static final String EXTRA_TO_WORD = "EXTRA_TO_WORD";
	private RandomActivity THIS = this;
	private WordDisplay mWordDisplay;
	private String mFromWord, mToWord;
	boolean activityStarted = false;
	
	private StarryBackgroundView mStarBackground;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random);
		
		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.randomActivity_background);

		TextView title = (TextView) findViewById(R.id.randomActivity_titleTextView);
		title.setTypeface(MyApplication.MAIN_FONT);

		TextView move = (TextView) findViewById(R.id.randomActivity_movesTextView);
		move.setTypeface(MyApplication.MAIN_FONT);
		move.setText(getString(R.string.startLevelActivity_moves) + " " + 10);

		this.mWordDisplay = (WordDisplay) findViewById(R.id.randomActivity_wordDisplay);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(RandomActivity.EXTRA_FROM_WORD))
			{
				this.mFromWord = extras.getString(PlayScreenActivity.EXTRA_FROM_WORD);
				this.mToWord = extras.getString(PlayScreenActivity.EXTRA_TO_WORD);
				this.mWordDisplay.set(null, null, this.mFromWord, this.mToWord);
			}
		}
		else
		{
			randomWords();
		}

		ImageButton randomButton = (ImageButton) findViewById(R.id.randomActivity_randomButton);
		randomButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				randomWords();
			}
		});
		
		ImageButton playButton = (ImageButton) findViewById(R.id.randomActivity_startButton);
		playButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent playIntent = new Intent(THIS, PlayScreenActivity.class);
				playIntent.putExtra(PlayScreenActivity.EXTRA_FROM_WORD, mFromWord);
				playIntent.putExtra(PlayScreenActivity.EXTRA_TO_WORD, mToWord);
				startActivity(playIntent);
				overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
				activityStarted = true;
				finish();
			}
		});

		ImageButton toMapButton = (ImageButton) findViewById(R.id.randomActivity_backToMapButton);
		toMapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(RandomActivity.TAG, "To map button clicked");
				activityStarted = true;
				finish();
			}
		});

	}
	
	private void randomWords()
	{
		this.mFromWord = WordHandler.randomWord();
		this.mToWord = this.mFromWord;
		while (this.mFromWord.equals(this.mToWord))
			this.mToWord = WordHandler.randomWord();
		this.mWordDisplay.set(null, null, this.mFromWord, this.mToWord);
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		activityStarted = false;
		MyApplication.playSong();
	//	MyApplication.mServ.resumeMusic();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.mWordDisplay.destroy();
		
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
