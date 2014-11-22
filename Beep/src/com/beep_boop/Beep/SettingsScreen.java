package com.beep_boop.Beep;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SettingsScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	public void returnfromsettings(View view){
		//this functions returns from the settings menu
		finish();
				
	}
	
}
