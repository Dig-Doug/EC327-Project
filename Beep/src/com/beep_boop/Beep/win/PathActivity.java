package com.beep_boop.Beep.win;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.stars.StarryBackgroundView;

public class PathActivity extends Activity
{
	/** Tag for logging */
	private static final String TAG = "PathActivity";
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	public static final String EXTRA_PATH = "EXTRA_PATH";
	private StarryBackgroundView mStarBackground;
	private String[] mPath;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path);
		MyApplication.activityStarted(this);

		this.mStarBackground = (StarryBackgroundView) findViewById(R.id.pathActivity_background);
		ImageButton nextButton = (ImageButton) findViewById(R.id.pathActivity_nextButton);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(WinActivity.EXTRA_LEVEL_KEY) && extras.containsKey(WinActivity.EXTRA_PATH))
			{
				//String levelKey = extras.getString(WinActivity.EXTRA_LEVEL_KEY);
				this.mPath = extras.getStringArray(WinActivity.EXTRA_PATH);
			}
			else
			{
				Log.w(PathActivity.TAG, "Missing bundle item");
			}
		}
		else
		{
			Log.e(PathActivity.TAG, "Error getting extras");
			finish();
		}
		
		TextView pathView = (TextView) findViewById(R.id.pathActivity_pathTextView);
		pathView.setLines(this.mPath.length);
		String pathText = "";
		for (int i = 0; i < this.mPath.length; i++)
		{
			String pathElement = this.mPath[i];
			if (i == this.mPath.length - 1)
			{
				String strColor = String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.winActivity_successTextColor));
				pathText += "<font color='";
				pathText += strColor;
				pathText += "'>" + pathElement +  "</font><br>";
			}
			else
			{
				pathText += pathElement +  "<br>";
			}
		}
		pathView.setText(Html.fromHtml(pathText));
		pathView.setTypeface(MyApplication.MAIN_FONT);
		pathView.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		MyApplication.activityPaused(this);

	}
	@Override
	protected void onRestart()
	{
		super.onRestart();
		MyApplication.mServ.resumeMusic();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (this.mStarBackground != null)
		{
			this.mStarBackground.destroy();
		}
	}
}
