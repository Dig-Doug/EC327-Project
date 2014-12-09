package com.beep_boop.Beep.lose;

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
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.random.RandomActivity;
import com.beep_boop.Beep.stars.StarryBackgroundView;
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class LoseActivity extends Activity
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	public static final String EXTRA_FROM_WORD = "EXTRA_FROM_WORD";
	public static final String EXTRA_TO_WORD = "EXTRA_TO_WORD";
	/** Tag for logging */
	private static final String TAG = "LoseActivity";
	private LoseActivity THIS = this;
	private Level mLevel;
	private String mFromWord, mToWord;
	
	private StarryBackgroundView mStarBackground;
	
	boolean activityStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lose);
		MyApplication.playSong();


		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.loseActivity_background);
		
		//MyApplication.activityStarted(this);

		
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
				this.mFromWord = extras.getString(LoseActivity.EXTRA_FROM_WORD);
				this.mToWord = extras.getString(LoseActivity.EXTRA_TO_WORD);
				this.mStarBackground.setBackgroundImage(R.drawable.random_background);
			}
		}
		else
		{
			Log.e(LoseActivity.TAG, "Error getting extras");
			finish();
		}
		
		this.setupButtons();
		
		TextView hintView = (TextView) findViewById(R.id.loseActivity_hintText);
		if (this.mLevel != null)
			hintView.setText(getString(R.string.loseActivity_hint) + " " + this.mLevel.hint);
		else
			hintView.setText(getString(R.string.loseActivity_hint) + " " + "This game is hard.");
		hintView.setTypeface(MyApplication.MAIN_FONT);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}
	
	private void setupButtons()
	{
		ImageButton restartLevelButton = (ImageButton) findViewById(R.id.loseActivity_restartLevelButton);
		restartLevelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mLevel != null)
				{
					Intent startLevelIntent = new Intent(THIS, StartLevelActivity.class);
					startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mLevel.levelKey);
					startActivity(startLevelIntent);
				}
				else
				{
					Intent randomIntent = new Intent(THIS, RandomActivity.class);
					randomIntent.putExtra(RandomActivity.EXTRA_FROM_WORD, mFromWord);
					randomIntent.putExtra(RandomActivity.EXTRA_TO_WORD, mToWord);
					startActivity(randomIntent);
				}
				activityStarted = true;
				finish();
			}
		});
		
		ImageButton mapButton = (ImageButton) findViewById(R.id.loseActivity_mapButton);
		mapButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activityStarted = true;
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
	@Override
	protected void onRestart(){
		super.onRestart();
		activityStarted = false;
		MyApplication.playSong();
	}
	@Override
	protected void onResume(){
		super.onResume();
		activityStarted = false;
		MyApplication.playSong();
	}
	@Override
	public void onBackPressed(){
		activityStarted = true;
		finish();
	
	}
}
