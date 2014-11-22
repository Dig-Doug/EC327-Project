package com.beep_boop.Beep.levelSelect;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.R;

public class MapView extends View
{
	///-----Interfaces-----
	public interface NodeClickListener
	{
		public boolean mapViewUserCanClickNode(MapView aMapView, MapNode aNode);
		public void mapViewUserDidClickNode(MapView aMapView, MapNode aNode);
	}

	public interface NodeStatusDataSource
	{
		public boolean mapViewIsNodeDone(MapView aMapView, MapNode aNode);
	}

	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "MapView";

	/** Holds the listener who handles node clicks */
	private NodeClickListener mListener = null;
	/** Holds the data source */
	private NodeStatusDataSource mDataSource = null;

	/** Holds all the nodes on the map */
	private ArrayList<MapNode> mNodes = new ArrayList<MapNode>();
	/** Holds the status of all the nodes */
	private ArrayList<Boolean> mNodeStates = new ArrayList<Boolean>();
	/** Holds the currently selected node */
	private int mSelectedNode = -1;

	/** Holds the view origin in map space */
	private PointF mOrigin = new PointF(0.0f, 0.0f);

	/** Holds the bounds the origin can take in map space */
	private RectF mOriginBounds;

	/** Holds the minimum distance the finger must move to be considered a scroll */
	private static final float mMinScrollDelta = 5.0f;
	/** Holds the last registered point of the touch in screen coords */
	private PointF mLastTouchPoint;
	/** Holds whether or not we are scrolling */
	private boolean mScrolling; 
	/** Holds the maximum distance the finger can be from a node to click it*/
	private static final float mMaxNodeClickDistance = 10.0f;

	/** Holds the amount of the map on the screen width wise */
	private static final float MAP_ON_SCREEN_WIDTH = 1.0f;
	/** Holds the amount of the map on the screen height wise */
	private static final float MAP_ON_SCREEN_HEIGHT = 1.0f;

	/** Hold the image to be drawn in the background */
	private Bitmap mBackgroundImage; // This may need to be broken up into multiple images, in which case an array should be used

	/** Holds the OFF node image */
	private Bitmap mNodeImageOff;
	/** Holds the ON node image */
	private Bitmap mNodeImageOn;
	/** Holds an overlay for the selected node  */
	private Bitmap mSelectedNodeOverlay;
	/** Holds the time it takes to transition between the off and on node image for the selected node */
	//private static final float NODE_IMAGE_TRANSITION_TIME;
	/** Holds the current state of the node */
	private boolean mSelectedNodeState;
	/** Holds the time until the next switch */
	private float mSelectedNodeTime;


	private int mNodeSizeX, mNodeSizeY;
	private Paint mNodePaint;

	///-----Constructors-----
	public MapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapView, 0, 0);
		try
		{
			//mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
			//mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
		}
		finally
		{
			a.recycle();
		}
		
		this.init();
	}
	
	private void init()
	{
		mNodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mNodePaint.setColor(0xffffff);
	}

	///-----Functions-----
	//Implements LevelDoneListener interface

	//sets the listener
	public void setListener(NodeClickListener aListener)
	{
		this.mListener = aListener;
	}

	//sets the data source
	public void setDataSource(NodeStatusDataSource aDataSource)
	{
		this.mDataSource = aDataSource;
	}

	//adds a node
	public void addNode(MapNode aNode)
	{
		//add the node
		this.mNodes.add(aNode);
		//recalculate the bounding box
		this.calculateOriginBounds();
	}

	//adds multiple nodes
	public void addNodes(ArrayList<MapNode> aNodeArray)
	{
		//add all the nodes
		this.mNodes.addAll(aNodeArray);
		//recalculate the bounding box
		this.calculateOriginBounds();
	}

	//gets the state of a node from the DataSource
	private boolean getStateForNode(MapNode aNode)
	{
		return this.mDataSource.mapViewIsNodeDone(this, aNode);
	}

	//sets the selected node
	public void setSelectedNode(int aIndex)
	{
		//set the selected node index
		this.mSelectedNode = aIndex;
	}

	//calculates the max and min origin bounds
	private void calculateOriginBounds()
	{
		float minX = 0.0f, minY = 0.0f;
		float maxX = 0.0f, maxY = 0.0f;
		if (this.mNodes.size() != 0)
		{
			//set initial values for the minds
			minX = this.mNodes.get(0).getX();
			minY = this.mNodes.get(0).getY();
			maxX = this.mNodes.get(0).getX();
			maxY = this.mNodes.get(0).getY();
			//find the max & min X & Y values
			for (MapNode node : this.mNodes)
			{
				if (node.getX() < minX)
				{
					minX = node.getX();
				}
				else if (node.getX() > maxX)
				{
					maxX = node.getX();
				}
				if (node.getY() < minY)
				{
					minY = node.getY();
				}
				else if (node.getY() > maxY)
				{
					maxY = node.getY();
				}
			}
		}

		//subtract half the screen so that all nodes can be centered on
		minX -= MapView.MAP_ON_SCREEN_WIDTH/2;
		minY -= MapView.MAP_ON_SCREEN_HEIGHT/2;
		maxX += MapView.MAP_ON_SCREEN_WIDTH/2;
		maxY += MapView.MAP_ON_SCREEN_HEIGHT/2;

		//set the new bounds
		this.mOriginBounds = new RectF(minX, minY, maxX, maxY);
	}

	//ensures the view�s origin is within the bounds
	private void boundOrigin()
	{
		//bound in the x direction
		if (this.mOrigin.x < this.mOriginBounds.left)
		{
			this.mOrigin.x = this.mOriginBounds.left;
		}
		else if (this.mOrigin.x > this.mOriginBounds.right)
		{
			this.mOrigin.x = this.mOriginBounds.right;
		}

		//bound in the y direction
		if (this.mOrigin.y < this.mOriginBounds.bottom)
		{
			this.mOrigin.y = this.mOriginBounds.bottom;
		}
		else if (this.mOrigin.y > this.mOriginBounds.top)
		{
			this.mOrigin.y = this.mOriginBounds.top;
		}
	}

	//sets the origin of the view
	public void setOrigin(PointF aOrigin)
	{
		//set the origin
		this.mOrigin = aOrigin;
		//make sure the origin is within bounds
		this.boundOrigin();
		//redraw
		this.requestRedraw();
	}

	//centers the view on the node
	private void centerOnNode(int aIndex)
	{
		//get the map we want to center on
		MapNode centerOn = this.mNodes.get(aIndex);
		//calculate the origin to center on it
		PointF centered = new PointF(centerOn.getX() - MapView.MAP_ON_SCREEN_WIDTH/2, centerOn.getY() - MapView.MAP_ON_SCREEN_HEIGHT/2);
		//set the origin
		this.setOrigin(centered);
	}

	//overridden view method
	protected void onDraw(Canvas canvas)
	{
		//draw background
		this.drawBackground(canvas);
		//draw all the nodes on top
		this.drawNodesWithinView(canvas);
	}

	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		//@TODO
	}

	//draws all the nodes that are within the bounds of the screen
	private void drawNodesWithinView(Canvas canvas)
	{
		//@TODO

		//enumerate through all map nodes
		for (int i = 0; i < this.mNodes.size(); i++)
		{
			MapNode node = this.mNodes.get(i);
			//check if it's on screen in the X direction
			if (Math.abs(node.getX() - this.mOrigin.x) < MapView.MAP_ON_SCREEN_WIDTH)
			{
				//check if it's on screen in the Y direction
				if (Math.abs(node.getY() - this.mOrigin.y) < MapView.MAP_ON_SCREEN_HEIGHT)
				{
					//draw it
					PointF screenDrawCenter = this.convertToScreenSpace(node.getX(), node.getY());

					if (i == this.mSelectedNode)
					{
						//@TODO - animate the nodes
					}
					else
					{
						//get which bitmap to use for this node
						//boolean state = this.mNodeStates.get(i).booleanValue();
						//Bitmap useToDraw = (state ? this.mNodeImageOn : this.mNodeImageOff);
						//canvas.drawBitmap(useToDraw, screenDrawCenter.x - this.mNodeSizeX, screenDrawCenter.y - this.mNodeSizeY, this.mNodePaint);
						canvas.drawCircle(screenDrawCenter.x, screenDrawCenter.y, 1.0f, this.mNodePaint);
					}
				}
			}
		}
	}

	//tells the map view to update all of the node�s states and redraw
	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}

	//gets touch events for view
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//result holds if the touch was processed by the view, which is all cases is yes
		boolean result = true;
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//handle touch down
			this.touchDown(event);
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			//handle touch moved
			this.touchMoved(event);
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			//handle touch up
			this.touchUp(event);
		}
		else
		{
			Log.e(MapView.TAG, "Unknown motion event type: " + event.getAction());
			result = false;
		}

		return result;
	}
	
	@Override
	public boolean performClick()
	{
		return super.performClick();
	}

	//handles all touch down events
	private void touchDown(MotionEvent aEvent)
	{
		this.mLastTouchPoint.x = aEvent.getX();
		this.mLastTouchPoint.y = aEvent.getY();
	}

	//handles all touch moved events
	private void touchMoved(MotionEvent aEvent)
	{
		float deltaX = aEvent.getX(), deltaY = aEvent.getY();

		//check if we are already scrolling
		if (this.mScrolling)
		{
			//increment the last touch point
			this.mLastTouchPoint.x += deltaX;
			this.mLastTouchPoint.y += deltaY;
			//calculate the delta movement in map space
			PointF deltaInMapSpace = this.convertToMapSpace(deltaX, deltaY);
			//increment the origin by the delta
			this.incrementOrigin(deltaInMapSpace.x, deltaInMapSpace.y);
		}
		else
		{
			//check if we've exceeded the minimum scroll distance
			if (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) <= Math.pow(MapView.mMinScrollDelta, 2))
			{
				//if so, we are scrolling
				this.mScrolling = true;
			}
		}
	}

	//increments the origin
	private void incrementOrigin(float aX, float aY)
	{
		//increment origin
		this.mOrigin.x += aX;
		this.mOrigin.y += aY;
		//make sure the origin is within bounds
		this.boundOrigin();
		//redraw
		this.requestRedraw();
	}

	//handles all touch up events
	private void touchUp(MotionEvent aEvent)
	{
		//check if we are scrolling
		if (this.mScrolling)
		{
			//do nothing
		}
		else
		{
			//if we're not, convert the touch point to map space
			PointF mapSpace = this.convertToMapSpace(aEvent.getX(), aEvent.getY());

			//get the nearest node
			MapNode nodeNearLocation = this.getNodeNearPoint(mapSpace);
			if (nodeNearLocation != null)
			{
				//there is a node to click
				//check if we can click the node
				boolean canClick = this.mListener.mapViewUserCanClickNode(this, nodeNearLocation);
				if (canClick)
				{
					//user can click node
					Log.v(MapView.TAG, "User clicked node with key: " + nodeNearLocation.getLevelKey());
					//click the node
					this.mListener.mapViewUserDidClickNode(this, nodeNearLocation);
					//set this node as the selected node
					this.mSelectedNode = this.mNodes.indexOf(nodeNearLocation);
					//tell the view to center on that node
					this.centerOnNode(this.mSelectedNode);
				}
				else
				{
					//can't click node
					Log.v(MapView.TAG, "User cannot click node with key: " + nodeNearLocation.getLevelKey());

					//@TODO - play sound?
				}
			}
			else
			{
				//no node to click, do nothing
			}
		}

		//touch ended, reset all variables
		this.resetTouchVariables();
	}

	//converts a point in touch space to map space
	private PointF convertToMapSpace(float aX, float aY)
	{
		//@TODO - scale
		float scaledX = aX + this.mOrigin.x;
		float scaledY = aX + this.mOrigin.y;
		return new PointF(scaledX, scaledY);
	}

	private PointF convertToScreenSpace(float aX, float aY)
	{
		float scaledX = aX - this.mOrigin.x;
		float scaledY = aY - this.mOrigin.y;
		return new PointF(scaledX, scaledY);
	}

	//find a node near the location. If there isn�t a node, returns null
	private MapNode getNodeNearPoint(PointF aPoint)
	{
		MapNode result = null;
		for (MapNode node : this.mNodes)
		{
			double distance = Math.sqrt(Math.pow(aPoint.x - node.getX(), 2) + Math.pow(aPoint.y- node.getY(), 2));
			if (Math.abs(distance) <= MapView.mMaxNodeClickDistance)
			{
				result = node;
				break;
			}
		}

		return result;
	}

	//resets all touch variables
	private void resetTouchVariables()
	{
		//reset variables here as needed
		this.mScrolling = false;
		this.mLastTouchPoint = new PointF(-1.0f, -1.0f);
	}

}
