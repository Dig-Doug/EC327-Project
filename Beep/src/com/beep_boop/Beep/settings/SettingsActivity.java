package com.beep_boop.Beep.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.stars.StarryBackgroundView;

public class SettingsActivity extends Activity
{
	boolean isPlaying = true;
	boolean activityStarted = false;
	private StarryBackgroundView mStarBackground;
	SeekBar volume;
	AudioManager audio = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		MyApplication.playSong();
		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.settingsActivity_background);
		RadioGroup fontGroup = (RadioGroup) findViewById(R.id.settingsActivity_fontGroup);
		//set the fonts of the radio buttons
		int count = fontGroup.getChildCount();
		int buttonCount = 0;
		for (int i = 0; i < count; i++)
		{
			View o = fontGroup.getChildAt(i);
			if (o instanceof RadioButton)
			{
				RadioButton button = (RadioButton)o;
				String fontName = getResources().getStringArray(R.array.fonts)[buttonCount];
				Typeface font = Typeface.createFromAsset(getAssets(), fontName);
				button.setTypeface(font);
				button.setText(getResources().getStringArray(R.array.fontNames)[buttonCount]);
				buttonCount++;
				if (fontName.equals(MyApplication.PLAY_FONT_NAME))
				{
					button.setChecked(true);
				}
				else
				{
					button.setChecked(false);
				}
			}
		}
		fontGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				int index = group.indexOfChild(group.findViewById(checkedId));
				MyApplication.changeFont(index);
			}
		});
		ImageButton backButton = (ImageButton) findViewById(R.id.settingsActivity_backButton);
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
		
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		volume= (SeekBar)findViewById(R.id.settingsActivity_musicBar);
        volume.setMax(audio

                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        volume.setProgress(audio

                .getStreamVolume(AudioManager.STREAM_MUSIC));

		volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		    audio.setStreamVolume(AudioManager.STREAM_MUSIC,

		                progress, 0);
		    }
		        //  Notify that the progress level has changed.

		       // textView.setText(textView.getText()+"\n"+"SeekBar now at the value of:"+progress);
/*
		    findViewById(R.id.settingsActivity_musicOn).setOnClickListener(new View.OnClickListener(){
		    	@Override
		    	public void onClick(View view){
		    		if(isPlaying){
		    				isPlaying = false;
		    				MyApplication.pauseSong();
		    				MyApplication.musicOn = false;
		    				
		    				
		    		}
		    		else{
		    			isPlaying = true;
		    			MyApplication.musicOn = true;
		    			MyApplication.playSong();
		    		}
		    	}

		    	});

		    }
			*/


		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    }



		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    }
			
			
		});
		 findViewById(R.id.settingsActivity_musicOn).setOnClickListener(new View.OnClickListener(){
		    	@Override
		    	public void onClick(View view){
		    		if(isPlaying){
		    				isPlaying = false;
		    				MyApplication.pauseSong();
		    				MyApplication.musicOn = false;
		    				
		    				
		    		}
		    		else{
		    			isPlaying = true;
		    			MyApplication.musicOn = true;
		    			MyApplication.playSong();
		    		}
		    	}

		    	});
	}


	
	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
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
		MyApplication.playSong();
	}
	@Override
	protected void onResume(){
		super.onResume();
		MyApplication.playSong();
	}
	@Override
	public void onBackPressed(){
		activityStarted = true;
		finish();
	}
}
