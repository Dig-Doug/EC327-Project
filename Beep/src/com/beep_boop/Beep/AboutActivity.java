package com.beep_boop.Beep;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AboutActivity extends Activity
{
	private TextView mMessageView;
	private EditText mMessageEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		mMessageView = (TextView) findViewById(R.id.aboutActivity_messageView);
		
		mMessageEditText = (EditText) findViewById(R.id.aboutActivity_messageEditText);
		
		Button showMessage = (Button) findViewById(R.id.aboutActivity_showMessage);
		showMessage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//set the edit text's text to the message text view
				String message = mMessageEditText.getText().toString();
				mMessageView.setText(message);
			}	
		});
	}
}
