package com.beep_boop.Beep.eggs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class RatingsPopupMessage extends Dialog
{
	private RatingsPopupMessage PAUSE_THIS = this;
	public RatingsPopupMessage (final Context context)
	{
		super(context, R.style.TransparentDialog);

		// This is the layout XML file that describes your Dialog layout
		this.setContentView(R.layout.dialog_pop_up_rating);
		getWindow().setBackgroundDrawableResource(R.color.transparent);

		TextView title = (TextView) findViewById(R.id.dialog_popUp_titleTextView);
		title.setTypeface(MyApplication.MAIN_FONT);
		title.setText(context.getString(R.string.mapActivity_ratingPopUpTitle));

		TextView message = (TextView) findViewById(R.id.dialog_popUp_messageTextView);
		message.setTypeface(MyApplication.MAIN_FONT);
		message.setText(context.getString(R.string.mapActivity_ratingPopUpMessage));

		ImageButton closeButton = (ImageButton) findViewById(R.id.dialog_popUp_backButton);
		closeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				PAUSE_THIS.dismiss();
			}
		});
		
		ImageButton yesButton = (ImageButton) findViewById(R.id.dialog_popUp_otherButton);
		yesButton.setVisibility(View.VISIBLE);
		yesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//open the app store
				final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
				
				SharedPreferences sharedPref = context.getSharedPreferences("MAIN", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(context.getString(R.string.prefs_hasRatedKey), true);
				editor.commit();
				
				//dismiss
				PAUSE_THIS.dismiss();
			}
		});
	}
}
