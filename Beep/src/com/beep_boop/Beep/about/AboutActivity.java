package com.beep_boop.Beep.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.stars.StarryBackgroundView;

public class AboutActivity extends Activity
{
	private StarryBackgroundView mStarBackground;
	boolean activityStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		MyApplication.playSong();


		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.aboutActivity_background);
		
		TextView aboutBlurb = (TextView) findViewById(R.id.aboutActivity_aboutText);
		aboutBlurb.setTypeface(MyApplication.MAIN_FONT);

		ImageButton backButton = (ImageButton) findViewById(R.id.aboutActivity_backButton);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activityStarted = true;
				finish();
				overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
			}
		});
	}

	@Override
	public void onBackPressed(){
		activityStarted = true;
		finish();
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
	protected void onDestroy()
	{
		super.onDestroy();
		//MyApplication.pauseSong();

		if (this.mStarBackground != null)
		{
			this.mStarBackground.destroy();
		}
	}
}
