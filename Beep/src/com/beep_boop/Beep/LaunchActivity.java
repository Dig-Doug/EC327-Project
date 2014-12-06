package com.beep_boop.Beep;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication.MusicService;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenParser;
import com.beep_boop.Beep.game.WordHandler;
import com.beep_boop.Beep.levelSelect.MapActivity;
import com.beep_boop.Beep.levels.LevelManager;

public class LaunchActivity extends Activity 
{
	
	///-----Member Variables-----
	/** Holds a reference to THIS for use in listeners */
	private LaunchActivity THIS = this;
	/** Holds a reference to a image view */
	private ImageView mLogoImageView;
	/** Holds a reference to a image view */
	private ImageView mTextImageView;
	
	private TextView mLoadingTextView;

	private boolean mLevelsLoaded = false, mWordsLoaded = false;
	private float mLevelsPercent = 0.0f, mWordsPercent = 0.0f;

	private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon =new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
	     binder) {
		mServ = ((MusicService.ServiceBinder)binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
		};

		void doBindService(){
	 		bindService(new Intent(this,MusicService.class),
					Scon,Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}

		void doUnbindService()
		{
			if(mIsBound)
			{
				unbindService(Scon);
	      		mIsBound = false;
			}
		}
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		//grab the image views from XML
		this.mLogoImageView = (ImageView) findViewById(R.id.launchActivity_logoImageView);
		this.mTextImageView = (ImageView) findViewById(R.id.launchActivity_textImageView);
		this.mLoadingTextView = (TextView) findViewById(R.id.launchActivity_loadingText);

		new LoadLevelsTask().execute(this);
		new LoadWordsTask().execute(this);
		
		doBindService();
		Intent music = new Intent();
		music.setClass(this,MusicService.class);
		startService(music);
		//load the fade in animation
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.anim_fadein);
		//start the animation
		this.mLogoImageView.startAnimation(fadeInAnimation);
		this.mTextImageView.startAnimation(fadeInAnimation);
		this.mLoadingTextView.startAnimation(fadeInAnimation);
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
				//do nothing
			}
		});
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
	
	private void updateLoadingProgress()
	{
		float percent = (this.mLevelsPercent + this.mWordsPercent) / 2 * 100;
		this.mLoadingTextView.setText(getString(R.string.launchActivity_loadingText) + " " + percent);
	}

	private void checkDone()
	{
		if (mWordsLoaded && mLevelsLoaded)
		{
			this.mLoadingTextView.setAlpha(0.0f);
			
			//load the fade out animation
			Animation fadeOutAnimation = AnimationUtils.loadAnimation(THIS, R.animator.anim_fadeout);
			//start the animation
			mLogoImageView.startAnimation(fadeOutAnimation);
			mTextImageView.startAnimation(fadeOutAnimation);
			//set the listener
			fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() 
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
					//make sure the image view doesn't reappear after the animation is done
					mLogoImageView.setAlpha(0.0f);
					mTextImageView.setAlpha(0.0f);

					//transition to map page
					Intent toMap = new Intent(THIS, MapActivity.class);
					startActivity(toMap);

					//quit
					finish();
				}
			});
		}
	}
}
