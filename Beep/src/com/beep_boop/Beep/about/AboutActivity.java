package com.beep_boop.Beep.about;

import android.app.Activity;
import android.os.Bundle;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class AboutActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		MyApplication.activityStarted(this);
	}
	@Override
	protected void onStop(){
		super.onStop();
		MyApplication.activityPaused(this);
		
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		MyApplication.mServ.resumeMusic();
		
	}
}
