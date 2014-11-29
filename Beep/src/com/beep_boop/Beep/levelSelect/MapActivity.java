package com.beep_boop.Beep.levelSelect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beep_boop.Beep.R;
import com.beep_boop.Beep.about.AboutActivity;
import com.beep_boop.Beep.levelSelect.MapView.NodeClickListener;
import com.beep_boop.Beep.levels.LevelManager;
import com.beep_boop.Beep.levels.LevelManager.LevelStateListener;
import com.beep_boop.Beep.settings.SettingsActivity;
import com.beep_boop.Beep.startScreen.StartLevelActivity;

public class MapActivity extends Activity implements NodeClickListener, LevelStateListener
{
	///-----Member Variables-----
	/** Tag used in Log messages */
	private static final String TAG = "MapActivity";
	/** Holds a reference to the map view */
	private MapView mMapView; 
	/** Reference to this */
	private Activity THIS = this;
	
	///-----Activity Life Cycle-----
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		//subscribe to level state updates
		LevelManager.addLevelStateListener(this);
		
		//get the map view from XML
		mMapView = (MapView)findViewById(R.id.mapActivity_mapView);
		//setup the map view
		this.setupMapView();
		
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
			}
		});
		
		//setup the about button
		ImageButton toAboutButton = (ImageButton) findViewById(R.id.mapActivity_aboutButton);
		toAboutButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(MapActivity.TAG, "To about button clicked");
				
				Intent toAbout = new Intent(THIS, AboutActivity.class);
				startActivity(toAbout);
			}
		});
	}
	
	private void setupMapView()
	{
		//load the nodes
		ArrayList<MapNode> nodeList = null;
		InputStream in = null;
		try 
		{
			in = getResources().openRawResource(R.raw.nodes_test_file);
			nodeList = MapNodeLoader.parseFile(in);
		}
		catch (Exception i)
		{
			Log.e("fileinput", "The IOException was caught.");
		}
		finally 
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					
				}
			}
		}
		
		//add the nodes to the map view
		this.mMapView.addNodes(nodeList); 
		
		//set the node click listener
		this.mMapView.setListener(this);
		
		this.mMapView.setSelectedNode(nodeList.get(0));
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//unsubscribe to level state updates
		LevelManager.removeLevelStateListener(this);
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
		Intent startLevelIntent = new Intent(this, StartLevelActivity.class);
		startLevelIntent.putExtra(StartLevelActivity.EXTRA_LEVEL_KEY, aNode.getLevelKey());
		startActivity(startLevelIntent);
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
