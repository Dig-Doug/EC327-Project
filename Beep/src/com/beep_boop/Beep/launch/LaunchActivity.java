package com.beep_boop.Beep.launch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.beep_boop.Beep.LevelManager;
import com.beep_boop.Beep.MainActivity;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.WordHandler;

public class LaunchActivity extends Activity 
{
	///-----Member Variables-----
	/** Holds a reference to THIS for use in listeners */
	private LaunchActivity THIS = this;
	/** Holds a reference to a image view */
	private ImageView logo_image_view;
	/** Holds a reference to a image view */
	private ImageView text_image_view;

	private boolean mLevelsLoaded, mWordsLoaded;
	
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		//grab the image views from XML
		logo_image_view = (ImageView) findViewById(R.id.launchActivity_logoImageView);
		text_image_view = (ImageView) findViewById(R.id.launchActivity_textImageView);
		
		new LoadLevelsTask().execute(this);
		new LoadWordsTask().execute(this);

		//load the fade in animation
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.anim_fadein);
		//start the animation
		logo_image_view.startAnimation(fadeInAnimation);
		text_image_view.startAnimation(fadeInAnimation);
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
				//wait while the tasks finish
				while (!mLevelsLoaded && !mWordsLoaded);
				
				//load the fade out animation
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(THIS, R.animator.anim_fadeout);
				//start the animation
				logo_image_view.startAnimation(fadeOutAnimation);
				text_image_view.startAnimation(fadeOutAnimation);
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
						logo_image_view.setAlpha(0.0f);
						text_image_view.setAlpha(0.0f);
						
						//transition to map page
						Intent switchToMain = new Intent(THIS, MainActivity.class);
						startActivity(switchToMain);
						
						//quit
						finish();
					}
				});
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
	         
	     }

	     protected void onPostExecute(Void result)
	     {
	    	 mLevelsLoaded = true;
	     }
	 }
	
	private class LoadWordsTask extends AsyncTask<Context, Void, Void>
	{
	     protected Void doInBackground(Context... contexts)
	     {
	    	 WordHandler.load(contexts[0]);
	         return null;
	     }

	     protected void onProgressUpdate(Void... voids)
	     {
	         
	     }

	     protected void onPostExecute(Void result)
	     {
	    	 mLevelsLoaded = true;
	     }
	 }
}
