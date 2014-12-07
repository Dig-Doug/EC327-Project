package com.beep_boop.Beep.settings;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.stars.StarryBackgroundView;

public class SettingsActivity extends Activity
{
	private StarryBackgroundView mStarBackground;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
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
				finish();
				overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
			}
		});
	}

	@Override
	protected void onStop(){
		super.onStop();
		
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.mStarBackground.destroy();
	}


}
