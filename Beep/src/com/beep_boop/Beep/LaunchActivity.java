package com.beep_boop.Beep;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beep_boop.Beep.game.PlayScreenParser;
import com.beep_boop.Beep.game.WordHandler;
import com.beep_boop.Beep.levelSelect.MapActivity;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.stars.StarryBackgroundView;
//import com.beep_boop.Beep.MyApplication.MusicService;

public class LaunchActivity extends Activity 
{
	
	///-----Member Variables-----
	/** Holds a reference to THIS for use in listeners */
	private LaunchActivity THIS = this;
	private ImageButton mStartButton;
	private ProgressBar mLoadingSpinner;
	private TextView mLoadingText;
	
	private boolean mLevelsLoaded = false, mWordsLoaded = false, mStarted = false;
	private float mLevelsPercent = 0.0f, mWordsPercent = 0.0f;

	private StarryBackgroundView mStarBackground;
	
	
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.launchActivity_background);

		//grab the image views from XML
		this.mStartButton = (ImageButton) findViewById(R.id.launchActivity_startButton);
		this.mStartButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showLoading();
			}
		});
		
		this.mLoadingSpinner = (ProgressBar) findViewById(R.id.launchActivity_loadingSpinner);
		this.mLoadingText = (TextView) findViewById(R.id.launchActivity_loadingText);
		
		this.mStartButton.setEnabled(false);
		this.hideLoading();

		new LoadLevelsTask().execute(this);
		new LoadWordsTask().execute(this);
		
		//load the fade in animation
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.anim_fadein);
		//start the animation
		this.mStartButton.startAnimation(fadeInAnimation);
		//set the listener
		fadeInAnimation.setAnimationListener(new Animation.AnimationListener() 
		{

			@Override
			public void onAnimationStart(Animation animation) 
			{
				// do nothing

			}

			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// do nothing, does not repeat

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				enableButtons();
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		this.mStarBackground.destroy();
	}

	private void enableButtons()
	{
		this.mStartButton.setEnabled(true);
	}
	
	private void showLoading()
	{
		this.mStarted = true;
		this.mStartButton.setVisibility(View.GONE);
		this.mLoadingSpinner.setVisibility(View.VISIBLE);
		this.mLoadingText.setVisibility(View.VISIBLE);
		this.mLoadingText.setTypeface(MyApplication.MAIN_FONT);
		this.checkDone();
	}
	
	private void hideLoading()
	{
		this.mLoadingSpinner.setVisibility(View.INVISIBLE);
		this.mLoadingText.setVisibility(View.INVISIBLE);
	}
	
	private void updateLoadingProgress()
	{
		int percent = (int)((this.mLevelsPercent + this.mWordsPercent) / 2 * 100);
		this.mLoadingText.setText(getString(R.string.launchActivity_loadingText) + " " + percent + "%");
	}

	private void checkDone()
	{
		if (mWordsLoaded && mLevelsLoaded && mStarted)
		{
			this.hideLoading();
			
			//transition to map page
			Intent toMap = new Intent(THIS, MapActivity.class);
			startActivity(toMap);
			overridePendingTransition(R.animator.anim_activity_top_in, R.animator.anim_activity_top_out);
			//quit
			finish();
		}
	}

	private class LoadLevelsTask extends AsyncTask<Context, Void, Void>
	{
		protected Void doInBackground(Context... contexts)
		{
			LevelManager.load(contexts[0]);
			return null;
		}

		protected void onProgressUpdate(Void... voids)
		{
			updateLoadingProgress();
		}

		protected void onPostExecute(Void result)
		{
			mLevelsLoaded = true;
			mLevelsPercent = 1.0f;

			checkDone();
		}
	}

	private class LoadWordsTask extends AsyncTask<Context, String, Void> implements PlayScreenParser.StatusUpdate
	{
		protected Void doInBackground(Context... contexts)
		{
			WordHandler.load(contexts[0], this);
			return null;
		}

		protected void onProgressUpdate(String... words)
		{
			updateLoadingProgress();
		}

		protected void onPostExecute(Void result)
		{
			mWordsLoaded = true;
			mWordsPercent = 1.0f;

			checkDone();
		}

		@Override
		public void parserStatusUpdate(int aIndex, String aWord)
		{
			mWordsPercent = aIndex / 5845.0f;
			publishProgress(aWord);
		}
	}
}
