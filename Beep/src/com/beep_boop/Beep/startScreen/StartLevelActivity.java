package com.beep_boop.Beep.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.beep_boop.Beep.AboutActivity;
import com.beep_boop.Beep.R;

public class StartLevelActivity extends Activity {
	
	private static final String TAG = "StartLevelActivity";
	private Activity THIS = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_level);
		
		Button toMapButton = (Button) findViewById(R.id.startLevelBackButton);
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
