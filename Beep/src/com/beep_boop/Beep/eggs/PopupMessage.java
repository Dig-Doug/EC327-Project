package com.beep_boop.Beep.eggs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

///-----Pause Menu Fragment Dialog-----
public class PopupMessage extends Dialog
{
	private PopupMessage PAUSE_THIS = this;
	public PopupMessage (final Context context, String aTitle, String aMessage)
	{
		super(context, R.style.TransparentDialog);

		// This is the layout XML file that describes your Dialog layout
		this.setContentView(R.layout.dialog_pop_up);
		getWindow().setBackgroundDrawableResource(R.color.transparent);

		TextView title = (TextView) findViewById(R.id.dialog_popUp_titleTextView);
		title.setTypeface(MyApplication.MAIN_FONT);
		title.setText(aTitle);

		TextView message = (TextView) findViewById(R.id.dialog_popUp_messageTextView);
		message.setTypeface(MyApplication.MAIN_FONT);
		message.setText(aMessage);
		
		ImageButton otherButton = (ImageButton) findViewById(R.id.dialog_popUp_otherButton);
		otherButton.setVisibility(View.GONE);

		ImageButton closeButton = (ImageButton) findViewById(R.id.dialog_popUp_backButton);
		closeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				PAUSE_THIS.dismiss();
			}
		});
	}
}