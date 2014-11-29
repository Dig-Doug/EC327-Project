package com.beep_boop.Beep.game;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.beep_boop.Beep.R;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.startScreen.StartLevelActivity;
import com.beep_boop.Beep.win.WinActivity;

public class PlayScreenActivity extends Activity implements PlayView.WordClickListener, PlayView.WordDataSource
{
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	/** Tag for logging */
	private static final String TAG = "PlayScreenActivity";
	/** Holds a reference to the play view */
	private PlayView mPlayView;
	
	private ArrayList<String> mWordPath = new ArrayList<String>();
	private Level mSelectedLevel;
	
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_screen);
		
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(StartLevelActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(StartLevelActivity.EXTRA_LEVEL_KEY);
				this.mSelectedLevel = LevelManager.getLevelForKey(levelKey);
			}
		}
		else
		{
			Log.e(PlayScreenActivity.TAG, "Error getting extras");
			finish();
		}
		
		this.mPlayView = (PlayView) findViewById(R.id.playScreenActivity_playView);	
		this.mPlayView.setListener(this);
		this.mPlayView.setDataSource(this);
		this.mPlayView.setCurrentWord(this.mSelectedLevel.fromWord);
		this.mWordPath.add(this.mSelectedLevel.fromWord);
	}

	///-----PlayView.WordDataSource methods-----
	@Override
	public Set<String> playViewWordsForWord(PlayView aPlayView, String aWord)
	{
		return WordHandler.getLinksForWord(aWord);
	}
	
	public String playViewPreviousWord(PlayView aPlayView)
	{
		return this.mWordPath.get(this.mWordPath.size() - 1);
	}

	///-----PlayView.WordClickListener methods-----
	@Override
	public void playViewUserDidClickWord(PlayView aPlayView, String aWord)
	{
		this.mWordPath.add(aWord);
		
		if (aWord.equalsIgnoreCase(this.mSelectedLevel.toWord))
		{
			Intent winIntent = new Intent(this, WinActivity.class);
			startActivity(winIntent);
			finish();
		}
	}
	
	public boolean playViewUserCanGoBack(PlayView aPlayView, String aCurrentWord)
	{
		return (this.mWordPath.size() > 1);
	}
	
	public void playViewUserDidGoBack(PlayView aPlayView)
	{
		this.mWordPath.remove(this.mWordPath.size() - 1);
	}
}
