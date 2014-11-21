package com.beep_boop.Beep.levelSelect;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapView
{
	///-----Interfaces-----
	public interface NodeClickListener
	{
	public void mapViewUserCanClickNode(MapView aMapView, MapNode aNode);
	public void mapViewUserDidClickNode(MapView aMapView, MapNode aNode);
	}
	
	public interface NodeStatusDataSource
	{
		public boolean mapViewNodeIsDone(MapNode aNode);
	}
	
	///-----Members-----
	/** Holds the listener who handles node clicks */
	private NodeClickListener mListener;
	/** Holds the data source */
	private NodeStatusDataSource mDataSource;

	/** Holds all the nodes on the map */
	private ArrayList<MapNode> mNodes;
	/** Holds the status of all the nodes */
	private ArrayList<Boolean> mNodeStates;
	/** Holds the currently selected node */
	private int mSelectedNode;

	/** Holds the view origin in map space */
	private PointF mOrigin;

	/** Holds the bounds the origin can take in map space */
	private RectF mOriginBounds;

	/** Holds the minimum distance the finger must move to be considered a scroll */
	private static final float mMinScrollDelta;
	/** Holds if we�re currently handling a touch */
	private boolean mTouched = false;
	/** Holds the starting point of a touch in screen coords */
	private Point mTouchStartPoint;
	/** Holds the last registered point of the touch in screen coords */
	private Point mTouchLastPoint;
	/** Holds whether or not we are scrolling */
	private boolean mScrolling; 
	/** Holds the maximum distance the finger can be from a node to click it*/
	private static final float mMaxNodeClickDistance;

	/** Holds the amount of the map on the screen width wise */
	private float mMapOnScreenWidth;
	/** Holds the amount of the map on the screen height wise */
	private float mMapOnScreenHeight;

	/** Hold the image to be drawn in the background
	private Bitmap mBackgroundImage; // This may need to be broken up into multiple images, in which case an array should be used

	/** Holds the OFF node image */
	private Bitmap mNodeImageOff;
	/** Holds the ON node image */
	private Bitmap mNodeImageOn;
	/** Holds an overlay for the selected node  */
	private Bitmap mSelectedNodeOverlay;
	/** Holds the time it takes to transition between the off and on node image for the selected node */
	private static final float NODE_IMAGE_TRANSITION_TIME;
	/** Holds the current state of the node */
	private boolean mSelectedNodeState;
	/** Holds the time until the next swtich */
	private float mSelectedNodeTime;

	///-----Constructors-----
	public MapView(Context context, AttributeSet attrs)
	{
		
	}
	
	///-----Functions-----
	//Implements LevelDoneListener interface

	//sets the listener
	public void setListener(NodeClickedListener aListener);
	//sets the data source
	public void setDataSource(NodeStatusDataSource aDataSource);

	//adds a node
	public void addNode(MapNode aNode);
	//adds multiple nodes
	public void addNodes(ArrayList aNodeArray);
	//gets the state of a node from the DataSource
	private boolean getStateForNode(MapNode aNode);

	//sets the selected node
	public void setSelectedNode(int aIndex);

	//calculates the max and min origin bounds
	private void calculateOriginBounds();
	//ensures the view�s origin is within the bounds
	private void boundOrigin();
	//sets the origin of the view
	public void setOrigin(PointF aOrigin);
	//centers the view on the node
	private void centerOnNode(int aIndex);

	//overriden view method
	protected void onDraw(Canvas canvas);
	//draws the background of the map
	private void drawBackground(Canvas canvas)
	//draws all the nodes that are within the bounds of the screen
	private void drawNodesWithinView(Canvas canvas);

	//handles scrolling
	private void scroll(float aX, float aY);
	//tells the mapview to update all of the node�s states and redraw
	public void invalidate();

	//gets touch events for view
	public boolean onTouchEvent(MotionEvent event)

	//handles all touch moved events
	private void touchMoved(MotionEvent aEvent);
	//increments the origin
	private void incrementOrigin(float aX, float aY);

	//handles all touch up events
	private void touchUp(MotionEvent aEvent);
	//converts a point in touch space to map space
	private Point convertToMapSpace(Point aPoint);
	//find a node near the location. If there isn�t a node, returns null
	private MapNode getNodeAtPoint(PointF aPoint);
	//resets all touch variables
	private void resetTouchVariables();

}
