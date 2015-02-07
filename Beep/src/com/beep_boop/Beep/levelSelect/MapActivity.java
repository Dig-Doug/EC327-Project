package com.beep_boop.Beep.levelSelect;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beep_boop.Beep.LaunchActivity;
import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.eggs.PopupMessage;
import com.beep_boop.Beep.levelSelect.MapView.NodeClickListener;
import com.beep_boop.Beep.levelSelect.MapView.NodeStatusDataSource;
import com.beep_boop.Beep.levels.Level;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.levels.LevelManager.LevelStateListener;
import com.beep_boop.Beep.random.RandomActivity;
import com.beep_boop.Beep.settings.SettingsActivity;
import com.beep_boop.Beep.startScreen.StartLevelActivity;
import com.beep_boop.Beep.statistics.StatisticsManager;

public class MapActivity extends Activity implements NodeClickListener, LevelStateListener, NodeStatusDataSource
{	
	///-----Member Variables-----
	public static final String EXTRA_GO_TO_LEVEL = "EXTRA_GO_TO_LEVEL";
	/** Tag used in Log messages */
	private static final String TAG = "MapActivity";
	/** Holds a reference to the map view */
	private MapView mMapView; 
	private TextView mLevelCount;
	/** Reference to this */
	private Activity THIS = this;

	boolean activityStarted = false;
	
	boolean mPopupOpen = false;

	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		//MyApplication.activityStarted = true;
		MyApplication.playSong();
		//subscribe to level state updates
		LevelManager.addLevelStateListener(this);

		//get the map view from XML
		mMapView = (MapView)findViewById(R.id.mapActivity_mapView);
		mLevelCount = (TextView) findViewById(R.id.mapActivity_levelCount);
		this.mLevelCount.setTypeface(MyApplication.MAIN_FONT);
		//setup the map view
		this.setupMapView();

		Bundle extras = this.getIntent().getExtras();
		if (extras != null)
		{
			if (extras.containsKey(MapActivity.EXTRA_GO_TO_LEVEL))
			{
				String levelKey = extras.getString(MapActivity.EXTRA_GO_TO_LEVEL);
				MapNode nodeForKey = this.mMapView.getNodeWithKey(levelKey);
				if (nodeForKey != null)
				{
					this.mMapView.setSelectedNode(nodeForKey, true, true);
				}
				else
				{
					Log.w(MapActivity.TAG, "Couldn't find map node for key: " + levelKey);
				}
			}
		}
		
		//setup the settings button
		ImageButton toSettingsButton = (ImageButton) findViewById(R.id.mapActivity_settingsButton);
		toSettingsButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MapActivity.TAG, "To settings button clicked");

				Intent toSettings = new Intent(THIS, SettingsActivity.class);
				startActivity(toSettings);
				activityStarted = true;
				overridePendingTransition(R.animator.anim_activity_top_in, R.animator.anim_activity_top_out);
			}
		});

		//setup the about button
		ImageButton toHomeButton = (ImageButton) findViewById(R.id.mapActivity_homeButton);
		toHomeButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MapActivity.TAG, "To home button clicked");

				Intent toLaunch = new Intent(THIS, LaunchActivity.class);
				startActivity(toLaunch);
				activityStarted = true;
				overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
				
				//quit map
				finish();
			}
		});
	}

	private void setupMapView()
	{
		//set the node click listener
		this.mMapView.setListener(this);
		this.mMapView.setDataSource(this);
		//add the nodes to the map view
		this.mMapView.addNodes(MapHandler.getNodes()); 
		this.mMapView.setSelectedNode(MapHandler.getNodes().get(0), false);
	}

	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}

	@Override
	protected void onStart(){
		super.onStart();
		activityStarted = false;
		MyApplication.playSong();
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		activityStarted = false;
		MyApplication.playSong();
	}

	@Override
	protected void onResume(){
		super.onResume();
		activityStarted = false;
		MyApplication.playSong();
		this.mLevelCount.setText(LevelManager.getTotalLevelsDone() + " / " + LevelManager.getTotalLevelsCount());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//unsubscribe to level state updates
		LevelManager.removeLevelStateListener(this);
		MyApplication.pauseSong();
		//MyApplication.mServ.stopMusic();


		this.mMapView.destroy();
	}

	///-----NodeClickListener methods-----
	public boolean mapViewUserCanClickNode(MapView aMapView, MapNode aNode)
	{
		//get if can play from level manager
		boolean result = LevelManager.canPlayLevel(aNode.getLevelKey());
		//check if result is no
		if (!result)
		{
			//display message
			Toast.makeText(this, getString(R.string.mapActivity_cannotClickNodeToastMessage), Toast.LENGTH_SHORT).show();
		}

		//return result
		return result;
	}

	public void mapViewUserDidClickNode(MapView aMapView, MapNode aNode)
	{
		Level selectedLevel = LevelManager.getLevelForKey(aNode.getLevelKey());
		if (!selectedLevel.easterEgg)
		{
			Intent startLevelIntent = new Intent(this, StartLevelActivity.class);
			startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, aNode.getLevelKey());
			startActivity(startLevelIntent);
		}
		else
		{
			/*
			if (selectedLevel.levelKey.equals("egg_credits"))
			{
				Intent toCredits = new Intent(THIS, CreditsActivity.class);
				startActivity(toCredits);
				activityStarted = true;
				overridePendingTransition(R.animator.anim_activity_top_in, R.animator.anim_activity_top_out);
			}
			else*/ if (selectedLevel.levelKey.equals("egg_random"))
			{
				Intent toRandom = new Intent(THIS, RandomActivity.class);
				startActivity(toRandom);
				activityStarted = true;
			}
			else
			{
				PopupMessage message = new PopupMessage(this, selectedLevel.fromWord, selectedLevel.hint);
				message.setOnDismissListener(new OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface arg0)
					{
						mPopupOpen = false;
					}
				});
				message.show();
				
				this.mPopupOpen = true;
				
				StatisticsManager.recordData(selectedLevel.levelKey, null, null, null, -1, true);
			}
		}
		activityStarted = true;
	}

	///-----NodeDataSource methods-----
	public boolean mapViewIsNodeDone(MapView aMapView, MapNode aNode)
	{
		return LevelManager.getIsLevelComplete(aNode.getLevelKey());
	}

	///-----LevelStateListener methods-----
	public void stateDidChangeForLevel(String aLevelKey, boolean aState)
	{
		this.mMapView.updateStateForNodeWithKey(aLevelKey, aState);
	}
}
