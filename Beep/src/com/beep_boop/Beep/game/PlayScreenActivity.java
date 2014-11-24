package com.beep_boop.Beep.game;

import android.app.Activity;
import android.os.Bundle;

import com.beep_boop.Beep.R;

public class PlayScreenActivity extends Activity
{
	///-----Member Variables-----
	/** Tag for logging */
	private static final String TAG = "PlayScreenActivity";
	/** Holds a reference to the play view */
	private PlayView mPlayView;
	/** Handles storing the words */
	private WordHandler mWordHandler;
	
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_screen);
		
		this.mWordHandler = new WordHandler(this);
		
		this.mPlayView = (PlayView) findViewById(R.id.playScreenActivity_playView);	
		this.mPlayView.setWords(this.mWordHandler.getLinksForWord("Matthew McConaughey"));
	}
	
	///-----
}
