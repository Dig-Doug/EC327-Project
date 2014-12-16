package com.beep_boop.Beep.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.lose.LoseActivity;
import com.beep_boop.Beep.random.RandomActivity;
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
	public static final String EXTRA_FROM_WORD = "EXTRA_FROM_WORD";
	public static final String EXTRA_TO_WORD = "EXTRA_TO_WORD";
	//private static final String PAUSE_MENU_TAG = "PAUSE_MENU_TAG";
	/** Tag for logging */
	private static final String TAG = "PlayScreenActivity";
	/** Holds a reference to the play view */
	private PlayView mPlayView;
	private GoalBar mGoalBar;

	private PlayScreenActivity THIS = this;


	private ArrayList<String> mWordPath = new ArrayList<String>();
	private ArrayList<String> mWordStack = new ArrayList<String>();
	private Level mSelectedLevel;
	private String mFromWord, mToWord;
	private double mStartTime;
	private boolean mPaused = false;
	private double mPauseTimeTotal = 0;
	private double mPauseStartTime = -1;
	private int mMovesLeft;
	boolean activityStarted = false;
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_screen);
		MyApplication.playSong();

		this.mPlayView = (PlayView) findViewById(R.id.playScreenActivity_playView);	
		this.mGoalBar = (GoalBar) findViewById(R.id.playScreenActivity_goalBar);

		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(PlayScreenActivity.EXTRA_LEVEL_KEY))
			{
				String levelKey = extras.getString(PlayScreenActivity.EXTRA_LEVEL_KEY);
				this.mSelectedLevel = LevelManager.getLevelForKey(levelKey);
				this.mFromWord = this.mSelectedLevel.fromWord;
				this.mToWord = this.mSelectedLevel.toWord;
				this.mMovesLeft = this.mSelectedLevel.maxMoves;
			}
			else
			{
				this.mFromWord = extras.getString(PlayScreenActivity.EXTRA_FROM_WORD);
				this.mToWord = extras.getString(PlayScreenActivity.EXTRA_TO_WORD);
				this.mMovesLeft = 10;

				this.mPlayView.setBackgroundImage(R.drawable.random_background);
				this.mGoalBar.setBackgroundImage(R.drawable.goal_bar_background_red);
			}
		}
		else
		{
			Log.e(PlayScreenActivity.TAG, "Error getting extras");
			finish();
		}

		this.mPlayView.setListener(this);
		this.mPlayView.setDataSource(this);
		this.mPlayView.setCurrentWord(this.mFromWord);
		this.mWordPath.add(this.mFromWord);

		this.mGoalBar.setListener(this);
		this.initGoalBar();

		this.mStartTime = System.currentTimeMillis();
	}
	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		activityStarted = false;
		MyApplication.playSong();

	}

	private void initGoalBar()
	{
		Bitmap fromBit = null, toBit = null;
		if (this.mSelectedLevel != null)
		{
			try
			{
				fromBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.fromImage));
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error getting from level image");
			}
			try
			{
				toBit = BitmapFactory.decodeStream(getAssets().open("level_images/" + this.mSelectedLevel.toImage));
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error getting to level image");
			}
		}
		this.mGoalBar.set(fromBit, toBit, this.mFromWord, this.mToWord);

		this.mGoalBar.numberOfClicksChanged(this.mMovesLeft);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//MyApplication.pauseSong();

		this.mGoalBar.destroy();
		this.mPlayView.destroy();
	}

	@Override
	protected void onResume(){
		super.onResume();
		activityStarted = false;
		MyApplication.playSong();
	}

	private void play()
	{
		this.mPaused = false;
		if (this.mPauseStartTime != -1)
		{
			double pausedTime = System.currentTimeMillis() - this.mPauseStartTime;
			this.mPauseTimeTotal += pausedTime;
			this.mPauseStartTime = -1;
		}
	}

	private void pause()
	{
		if (!this.mPaused)
		{
			this.mPauseStartTime = System.currentTimeMillis();
			PauseMenuDialogFragment dialog = new PauseMenuDialogFragment(this);//.show(getFragmentManager(), PAUSE_MENU_TAG);
			dialog.show();
		}
	}

	///-----PlayView.WordDataSource methods-----
	@Override
	public List<String> playViewWordsForWord(PlayView aPlayView, String aWord)
	{
		List<String> sorted = new ArrayList<String>();
		Collection<String> links = WordHandler.getLinksForWord(aWord);
		if (links != null)
		{
			sorted.addAll(links);
		}
		Collections.sort(sorted , String.CASE_INSENSITIVE_ORDER);
		return sorted;
	}

	public String playViewPreviousWord(PlayView aPlayView)
	{
		return this.mWordStack.get(this.mWordStack.size() - 1);
	}

	///-----PlayView.WordClickListener methods-----
	@Override
	public void playViewUserDidClickWord(PlayView aPlayView, String aWord)
	{
		this.mWordPath.add(aWord);
		this.mWordStack.add(aWord);
		this.mMovesLeft--;
		this.mGoalBar.numberOfClicksChanged(this.mMovesLeft);
		this.checkDone(aWord);
	}

	public boolean playViewUserCanGoBack(PlayView aPlayView, String aCurrentWord)
	{
		return (this.mWordPath.size() > 1);
	}

	public void playViewUserDidGoBack(PlayView aPlayView)
	{
		this.mWordPath.add(this.mWordPath.get(this.mWordPath.size() - 2));
		this.mWordStack.remove(this.mWordStack.size() - 1);
		//this.mMovesLeft--;
		//this.mGoalBar.numberOfClicksChanged(this.mMovesLeft);
		this.checkDone(null);
	}

	@Override
	public void onBackPressed()
	{
		if (this.mWordPath.size() > 1)
		{
			this.mPlayView.goBack();
		}
		else
		{
			super.onBackPressed();
			activityStarted = true;
		}
	}

	private void checkDone(String aWord)
	{
		if (aWord != null && aWord.equalsIgnoreCase(this.mToWord))
		{
			String[] pathArray = new String[this.mWordPath.size()];
			for (int i = 0; i < pathArray.length; i++)
			{
				String word = this.mWordPath.get(i);
				pathArray[i] = word;
			}
			
			Intent winIntent = new Intent(this, WinActivity.class);
			if (this.mSelectedLevel != null)
				winIntent.putExtra(WinActivity.EXTRA_LEVEL_KEY, this.mSelectedLevel.levelKey);
			else
			{
				winIntent.putExtra(WinActivity.EXTRA_FROM_WORD, this.mFromWord);
				winIntent.putExtra(WinActivity.EXTRA_TO_WORD, this.mToWord);
			}
			winIntent.putExtra(WinActivity.EXTRA_TIME, System.currentTimeMillis() - this.mStartTime - this.mPauseTimeTotal);
			winIntent.putExtra(WinActivity.EXTRA_PATH, pathArray);
			startActivity(winIntent);
			activityStarted = true;
			overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
			finish();
		}
		else if (this.mMovesLeft <= 0)
		{
			String[] pathArray = new String[this.mWordPath.size()];
			for (int i = 0; i < pathArray.length; i++)
			{
				String word = this.mWordPath.get(i);
				pathArray[i] = word;
			}
			
			Intent loseIntent = new Intent(this, LoseActivity.class);
			if (this.mSelectedLevel != null)
				loseIntent.putExtra(LoseActivity.EXTRA_LEVEL_KEY, this.mSelectedLevel.levelKey);
			else
			{
				loseIntent.putExtra(LoseActivity.EXTRA_FROM_WORD, this.mFromWord);
				loseIntent.putExtra(LoseActivity.EXTRA_TO_WORD, this.mToWord);
			}
			loseIntent.putExtra(LoseActivity.EXTRA_TIME, System.currentTimeMillis() - this.mStartTime - this.mPauseTimeTotal);
			loseIntent.putExtra(LoseActivity.EXTRA_PATH, pathArray);
			startActivity(loseIntent);
			activityStarted = true;
			overridePendingTransition(R.animator.anim_activity_left_in, R.animator.anim_activity_left_out);
			finish();
		}
	}

	///-----Goal Bar Click Listener-----
	@Override
	public void goalBarUserDidClick(GoalBar aPlayView)
	{
		this.pause();
	}


	///-----Pause Menu Fragment Dialog-----
	public class PauseMenuDialogFragment extends Dialog
	{
		private PauseMenuDialogFragment PAUSE_THIS = this;
		public PauseMenuDialogFragment (final Context context)
		{
			super(context, R.style.TransparentDialog);

			// This is the layout XML file that describes your Dialog layout
			this.setContentView(R.layout.dialog_play_pause_menu);
			getWindow().setBackgroundDrawableResource(R.color.transparent);

			if (mSelectedLevel == null)
			{
				TableLayout background = (TableLayout) findViewById(R.id.playScreenActivity_pauseMenu_background);
				background.setBackgroundResource(R.drawable.pause_menu_red);
			}

			TextView title = (TextView) findViewById(R.id.playScreenActivity_pauseMenu_titleTextView);
			title.setTypeface(MyApplication.MAIN_FONT);

			ImageButton playButton = (ImageButton) findViewById(R.id.playScreenActivity_pauseMenu_playButton);
			playButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					play();
					PAUSE_THIS.dismiss();
				}
			});

			ImageButton resetButton = (ImageButton) findViewById(R.id.playScreenActivity_pauseMenu_resetButton);
			resetButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mSelectedLevel != null)
					{
						Intent startLevelIntent = new Intent(THIS, StartLevelActivity.class);
						startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, mSelectedLevel.levelKey);
						startActivity(startLevelIntent);
					}
					else
					{
						Intent randomIntent = new Intent(THIS, RandomActivity.class);
						randomIntent.putExtra(RandomActivity.EXTRA_FROM_WORD, mFromWord);
						randomIntent.putExtra(RandomActivity.EXTRA_TO_WORD, mToWord);
						startActivity(randomIntent);
					}
					activityStarted =true;
					PAUSE_THIS.dismiss();
					finish();
					overridePendingTransition(R.animator.anim_activity_right_in, R.animator.anim_activity_right_out);

				}
			});

			ImageButton settingsButton = (ImageButton) findViewById(R.id.playScreenActivity_pauseMenu_settingsButton);
			settingsButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent settingsIntent = new Intent(THIS, SettingsActivity.class);
					startActivity(settingsIntent);
					activityStarted = true;
					overridePendingTransition(R.animator.anim_activity_top_in, R.animator.anim_activity_top_out);
				}
			});

			ImageButton mapButton = (ImageButton) findViewById(R.id.playScreenActivity_pauseMenu_mapButton);
			mapButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					activityStarted = true;
					THIS.finish();
				}
			});
		}

		@Override
		public void onBackPressed()
		{
			super.onBackPressed();
			play();
		}
	}
}
