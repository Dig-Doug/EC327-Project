package com.beep_boop.Beep.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.lose.LoseActivity;
import com.beep_boop.Beep.settings.SettingsActivity;
import com.beep_boop.Beep.startScreen.StartLevelActivity;
import com.beep_boop.Beep.win.WinActivity;

public class PlayScreenActivity extends Activity implements PlayView.WordClickListener, PlayView.WordDataSource, GoalBar.ClickListener
{
	
	public interface NumberOfClicksChangedListener
	{
		public void numberOfClicksChanged(int aNumberOfClicks);
	}
	///-----Member Variables-----
	public static final String EXTRA_LEVEL_KEY = "EXTRA_LEVEL_KEY";
	private static final String PAUSE_MENU_TAG = "PAUSE_MENU_TAG";
	/** Tag for logging */
	private static final String TAG = "PlayScreenActivity";
	/** Holds a reference to the play view */
	private PlayView mPlayView;
	private GoalBar mGoalBar;
	

	private ArrayList<String> mWordPath = new ArrayList<String>();
	private Level mSelectedLevel;
	private double mStartTime;
	private double mPauseTimeTotal = 0;
	private double mPauseStartTime = -1;

	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_screen);
		MyApplication.activityCreated(this);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(PlayScreenActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(PlayScreenActivity.EXTRA_LEVEL_KEY);
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

		this.mGoalBar = (GoalBar) findViewById(R.id.playScreenActivity_goalBar);
		this.mGoalBar.setListener(this);
		this.initGoalBar();

		this.mStartTime = System.currentTimeMillis();
	}
	@Override
	protected void onStop(){
		super.onStop();
		MyApplication.activityPaused(this);
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		MyApplication.mServ.resumeMusic();
	}
	
	private void initGoalBar()
	{
		Bitmap fromBit = null, toBit = null;
		try
		{
			fromBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.fromImage));
			toBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.toImage));
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error getting level images");
		}
		this.mGoalBar.set(fromBit, toBit, this.mSelectedLevel.fromWord, this.mSelectedLevel.toWord);

	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		this.mGoalBar.destroy();
		this.mPlayView.destroy();
	}
	
	
	private void play()
	{
		if (this.mPauseStartTime != -1)
		{
			double pausedTime = System.currentTimeMillis() - this.mPauseStartTime;
			this.mPauseTimeTotal += pausedTime;
			this.mPauseStartTime = -1;
		}
	}

	private void pause()
	{
		this.mPauseStartTime = System.currentTimeMillis();
		new PauseMenuDialogFragment().show(getFragmentManager(), PAUSE_MENU_TAG);
	}

	///-----PlayView.WordDataSource methods-----
	@Override
	public List<String> playViewWordsForWord(PlayView aPlayView, String aWord)
	{
		List<String> sorted = new ArrayList<String>();
		sorted.addAll(WordHandler.getLinksForWord(aWord));
		Collections.sort(sorted , String.CASE_INSENSITIVE_ORDER);
		return sorted;
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
		this.mGoalBar.numberOfClicksChanged(this.mWordPath.size()-1);
		if (aWord.equalsIgnoreCase(this.mSelectedLevel.toWord))
		{
			String[] pathArray = new String[this.mWordPath.size()];
			for (int i = 0; i < pathArray.length; i++)
			{
				String word = this.mWordPath.get(i);
				pathArray[i] = word;
			}

			Intent winIntent = new Intent(this, WinActivity.class);
			winIntent.putExtra(WinActivity.EXTRA_LEVEL_KEY, this.mSelectedLevel.levelKey);
			winIntent.putExtra(WinActivity.EXTRA_TIME, System.currentTimeMillis() - this.mStartTime - this.mPauseTimeTotal);
			winIntent.putExtra(WinActivity.EXTRA_PATH, pathArray);
			startActivity(winIntent);
			overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
			finish();
		}
		else if (this.mWordPath.size() > this.mSelectedLevel.maxMoves)
		{
			Intent loseIntent = new Intent(this, LoseActivity.class);
			loseIntent.putExtra(LoseActivity.EXTRA_LEVEL_KEY, this.mSelectedLevel.levelKey);
			startActivity(loseIntent);
			overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
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
		this.mGoalBar.numberOfClicksChanged(this.mWordPath.size()-1);
	}

	///-----Goal Bar Click Listener-----
	@Override
	public void goalBarUserDidClick(GoalBar aPlayView)
	{
		this.pause();
	}


	///-----Pause Menu Fragment Dialog-----
	public class PauseMenuDialogFragment extends DialogFragment
	{
		private PauseMenuDialogFragment PAUSE_THIS = this;

		@SuppressLint("InflateParams") @Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			View rootView = inflater.inflate(R.layout.dialog_play_pause_menu, null);
			builder.setView(rootView);

			ImageButton playButton = (ImageButton) rootView.findViewById(R.id.playScreenActivity_pauseMenu_playButton);
			playButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					play();
					PAUSE_THIS.dismiss();
				}
			});

			ImageButton resetButton = (ImageButton) rootView.findViewById(R.id.playScreenActivity_pauseMenu_resetButton);
			resetButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent startLevelIntent = new Intent(PAUSE_THIS.getActivity(), StartLevelActivity.class);
					startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mSelectedLevel.levelKey);
					startActivity(startLevelIntent);
					PAUSE_THIS.dismiss();
					finish();
					overridePendingTransition(R.animator.anim_activity_right_in, R.animator.anim_activity_right_out);
					
				}
			});

			ImageButton settingsButton = (ImageButton) rootView.findViewById(R.id.playScreenActivity_pauseMenu_settingsButton);
			settingsButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent settingsIntent = new Intent(PAUSE_THIS.getActivity(), SettingsActivity.class);
					startActivity(settingsIntent);
					overridePendingTransition(R.animator.anim_activity_top_in, R.animator.anim_activity_top_out);
				}
			});

			ImageButton mapButton = (ImageButton) rootView.findViewById(R.id.playScreenActivity_pauseMenu_mapButton);
			mapButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					PAUSE_THIS.getActivity().finish();
				}
			});

			return builder.create();
		}
		
		@Override
		public void onDismiss(DialogInterface dialog)
		{
			super.onDismiss(dialog);
			
			play();
		}
	}
}
