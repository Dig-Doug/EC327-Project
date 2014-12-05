package com.beep_boop.Beep.settings;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class SettingsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		
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
	}
	
	public void returnfromsettings(View view){
		//this functions returns from the settings menu
		finish();
		overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
	}
	
}
