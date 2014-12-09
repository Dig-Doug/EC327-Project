package com.beep_boop.Beep.levelSelect;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.stars.StarManager;

public class MapView extends View implements StarManager.ScreenSpaceCoverter
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

	private MapView THIS = this;

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
	private PointF mLastTouchPoint = new PointF();
	/** Holds whether or not we are scrolling */
	private boolean mScrolling; 
	/** Holds the maximum distance the finger can be from a node to click it*/
	private float mMaxNodeClickDistance = 0.05f;

	/** Holds the amount of the map on the screen width wise */
	private float MAP_ON_SCREEN_WIDTH = 1.0f;
	/** Holds the amount of the map on the screen height wise */
	private float MAP_ON_SCREEN_HEIGHT = 1.0f;

	private float mScaleX = 1.0f, mScaleY = 1.0f;
	/** Hold the image to be drawn in the background */
	private Bitmap[] mBackgroundImages; // This may need to be broken up into multiple images, in which case an array should be used

	private Bitmap mParrallaxImage;
	private float mParrallaxScale = 1.0f;

	/** Holds the OFF node image */
	private Bitmap mNodeImageOff;
	/** Holds the ON node image */
	private Bitmap mNodeImageOn;
	private int mNodeHalfSizeX, mNodeHalfSizeY;
	private int mOverlayHalfSizeX, mOverlayHalfSizeY;
	/** Holds the current state of the node */
	private float mSelectedNodeState;
	/** Holds the time it takes to transition between the off and on node image for the selected node */
	private int mAnimationLength;
	private Paint mNodeOnPaint, mNodeOffPaint;
	private ValueAnimator mNodeAnimator;

	/** Holds an overlay for the selected node  */
	private Bitmap mSelectedNodeOverlayStatic;
	private Bitmap mSelectedNodeOverlayAnimating;
	private ValueAnimator mSelectedOverlayAnimator;
	private int mSelectedOverlayAnimationLength;
	private float mSelectedOverlayAnimationPercent;
	private MapNode mSelectedOverlayAnimationStartNode;
	private MapNode mSelectedOverlayAnimationToNode;

	private static final float SCROLL_SCALAR = 2.0f;

	private int mBackgroundTotalHeight = 0;
	private boolean mBackgroundLoaded = false;

	private StarManager mStarManager;

	///-----Constructors-----
	public MapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapView, 0, 0);
		try
		{
			int imageArray = a.getResourceId(R.styleable.MapView_backgroundImage, -1);
			if (imageArray != -1)
			{
				new LoadBackgroundImagesTask().execute(imageArray);
			}

			float mapWidth = a.getFloat(R.styleable.MapView_mapWidthOnScreen, 1.0f);
			this.MAP_ON_SCREEN_WIDTH = mapWidth;
			int nodeOffImage = a.getResourceId(R.styleable.MapView_nodeOffImage, -1);
			this.mNodeImageOff = BitmapFactory.decodeResource(getResources(), nodeOffImage, null);
			int nodeOnImage = a.getResourceId(R.styleable.MapView_nodeOnImage, -1);
			this.mNodeImageOn = BitmapFactory.decodeResource(getResources(), nodeOnImage, null);
			int nodeOverlayStaticImage = a.getResourceId(R.styleable.MapView_nodeSelectedOverlayStatic, -1);
			if (nodeOverlayStaticImage != -1)
				this.mSelectedNodeOverlayStatic = BitmapFactory.decodeResource(getResources(), nodeOverlayStaticImage, null);
			int nodeOverlayAnimatingImage = a.getResourceId(R.styleable.MapView_nodeSelectedOverlayAnimating, -1);
			if (nodeOverlayAnimatingImage != -1)
				this.mSelectedNodeOverlayAnimating = BitmapFactory.decodeResource(getResources(), nodeOverlayAnimatingImage, null);
			this.mAnimationLength = a.getInteger(R.styleable.MapView_animationLength, 100);
			this.mMaxNodeClickDistance = a.getFloat(R.styleable.MapView_nodeClickDistance, 0.05f);
			this.mSelectedOverlayAnimationLength = a.getInteger(R.styleable.MapView_overlayAnimationLength, 1000);

			int parrallaxImage = a.getResourceId(R.styleable.MapView_parrallaxImage, -1);
			if (parrallaxImage != -1)
			{
				Bitmap cached = MyApplication.getBitmapFromMemCache(parrallaxImage + "");
				if (cached != null)
				{
					this.mParrallaxImage = cached;
				}
				else
				{
					this.mParrallaxImage = BitmapFactory.decodeResource(getResources(), parrallaxImage, null);
					MyApplication.addBitmapToMemoryCache(parrallaxImage + "", this.mParrallaxImage );
				}
			}

			int starArray = a.getResourceId(R.styleable.MapView_starImages, -1);
			if (starArray != -1)
			{
				TypedArray stars = getContext().getResources().obtainTypedArray(starArray);
				try
				{
					Bitmap[] starImages = new Bitmap[stars.length()];
					for (int i = 0; i < stars.length(); i++)
					{
						int bitmapID = stars.getResourceId(i, -1);
						Bitmap cached = MyApplication.getBitmapFromMemCache(bitmapID + "");
						if (cached != null)
						{
							starImages[i] = cached;
						}
						else
						{
							BitmapFactory.Options options = new BitmapFactory.Options();
							starImages[i] = BitmapFactory.decodeResource(getResources(), bitmapID, options);
							MyApplication.addBitmapToMemoryCache(bitmapID + "", starImages[i]);
						}
					}

					mStarManager = new StarManager(THIS, false, 100, starImages, new PointF(0.0f, 0f), 
							0.25f, 1.0f, 
							-5, 5, 
							0.01f, 0.99f, 
							0.05f, 0.2f);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					stars.recycle();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			a.recycle();
		}

		this.init();		
	}

	private class LoadBackgroundImagesTask extends AsyncTask<Integer, Void, Void>
	{
		protected Void doInBackground(Integer... ints)
		{
			int imageArray = ints[0];
			TypedArray imgs = getContext().getResources().obtainTypedArray(imageArray);

			try
			{
				mBackgroundImages = new Bitmap[imgs.length()];
				for (int i = 0; i < imgs.length(); i++)
				{
					int bitmapID = imgs.getResourceId(i, -1);
					if (bitmapID != -1)
					{
						Bitmap cached = MyApplication.getBitmapFromMemCache(bitmapID + "");
						if (cached != null)
						{
							mBackgroundImages[i] = cached;
						}
						else
						{
							BitmapFactory.Options options = new BitmapFactory.Options();
							mBackgroundImages[i] = BitmapFactory.decodeResource(getResources(), bitmapID, options);
							MyApplication.addBitmapToMemoryCache(bitmapID + "", mBackgroundImages[i]);
						}
						mBackgroundTotalHeight += mBackgroundImages[i].getHeight();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				imgs.recycle();
			}

			return null;
		}

		protected void onPostExecute(Void result)
		{
			mBackgroundLoaded = true;
			calculateBackground();
		}
	}

	private void init()
	{
		this.mNodeOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mNodeOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		// Create a new value animator that will use the range 0 to 1
		this.mNodeAnimator = ValueAnimator.ofFloat(0, 1);
		// It will take XXXms for the animator to go from 0 to 1
		this.mNodeAnimator.setDuration(this.mAnimationLength);
		// Callback that executes on animation steps. 
		this.mNodeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mSelectedNodeState = ((Float) (animation.getAnimatedValue())).floatValue();
				mNodeOffPaint.setAlpha((int)(255 * (1.0f - mSelectedNodeState)));
				mNodeOnPaint.setAlpha((int)(255 * mSelectedNodeState));
				requestRedraw();
			}
		});
		this.mNodeAnimator.setRepeatCount(ValueAnimator.INFINITE);
		this.mNodeAnimator.setRepeatMode(ValueAnimator.REVERSE);
		this.mNodeAnimator.start();
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		if (this.mStarManager != null)
		{
			this.mStarManager.start();
		}

		if (this.mNodeAnimator != null)
		{
			this.mNodeAnimator.start();
		}
	}

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();

		if (this.mStarManager != null)
		{
			this.mStarManager.pause();
		}

		if (this.mNodeAnimator != null)
		{
			this.mNodeAnimator.end();
		}
	}

	public void destroy()
	{
		if (this.mNodeImageOff != null)
		{
			this.mNodeImageOff.recycle();
			this.mNodeImageOff = null;
		}
		if (this.mNodeImageOn != null)
		{
			this.mNodeImageOn.recycle();
			this.mNodeImageOn = null;
		}
		for (int i = 0; i < this.mBackgroundImages.length; i++)
		{
			if (this.mBackgroundImages[i] != null)
			{
				//this.mBackgroundImages[i].recycle();
				this.mBackgroundImages[i] = null;
			}
		}
		if (this.mSelectedNodeOverlayStatic != null)
		{
			this.mSelectedNodeOverlayStatic.recycle();
			this.mSelectedNodeOverlayStatic = null;
		}
		if (this.mSelectedNodeOverlayAnimating != null)
		{
			this.mSelectedNodeOverlayAnimating.recycle();
			this.mSelectedNodeOverlayAnimating = null;
		}
		if (this.mParrallaxImage != null)
		{
			//this.mParrallaxImage.recycle();
			this.mParrallaxImage = null;
		}

		if (this.mStarManager != null)
		{
			this.mStarManager.destroy();
		}

		if (this.mNodeAnimator != null)
		{
			//clean up the animator
			this.mNodeAnimator.cancel();
			this.mNodeAnimator = null;
		}
	}

	///-----Functions-----
	//sets the listener
	public void setListener(NodeClickListener aListener)
	{
		this.mListener = aListener;
	}

	//sets the data source
	public void setDataSource(NodeStatusDataSource aDataSource)
	{
		this.mDataSource = aDataSource;

		this.updateStates();
	}

	//adds a node
	public void addNode(MapNode aNode)
	{
		//add the node
		this.mNodes.add(aNode);
		//recalculate the bounding box
		this.calculateOriginBounds();
		//update all states
		this.updateStates();
	}

	//adds multiple nodes
	public void addNodes(ArrayList<MapNode> aNodeArray)
	{
		//add all the nodes
		this.mNodes.addAll(aNodeArray);
		//recalculate the bounding box
		this.calculateOriginBounds();
		//update all states
		this.updateStates();
	}

	//gets the states for a node
	public void updateStateForNodeWithKey(String aLevelKey, boolean aState)
	{
		//look for the node
		for (int i = 0; i < this.mNodes.size(); i++)
		{
			MapNode node = this.mNodes.get(i);
			//check if it's the right node
			if (node.getLevelKey().equals(aLevelKey))
			{
				//save the state
				this.mNodeStates.set(i, aState);
				//break
				break;
			}
		}
	}

	//gets the states for all nodes
	public void updateStates()
	{
		//remove all old states
		this.mNodeStates.clear();
		//enumerate through all nodes
		for (MapNode node : this.mNodes)
		{
			//get state from data source
			boolean state = this.getStateForNode(node);
			//save data
			this.mNodeStates.add(state);
		}
	}

	//gets the state of a node from the DataSource
	private boolean getStateForNode(MapNode aNode)
	{
		boolean result = false;
		if (this.mDataSource != null)
		{
			result = this.mDataSource.mapViewIsNodeDone(this, aNode);
		}
		else
		{
			Log.w(MapView.TAG, "Datasource is null");
		}
		return result;
	}

	public void setSelectedNode(MapNode aNode, boolean aAnimated)
	{
		this.setSelectedNode(this.mNodes.indexOf(aNode), aAnimated, false);
	}

	//sets the selected node
	private void setSelectedNode(int aIndex, boolean aAnimated, boolean aShouldClick)
	{
		if (this.mSelectedNode != aIndex)
		{
			if (aAnimated)
			{
				//start the animation
				this.startAnimationToNode(this.mNodes.get(aIndex), aShouldClick);
			}
			else
			{
				this.mSelectedOverlayAnimationStartNode = this.mNodes.get(aIndex);
				this.mSelectedOverlayAnimationToNode = this.mNodes.get(aIndex);
			}

			//set the selected node index
			this.mSelectedNode = aIndex;
			//tell the view to center on that node
			this.centerOnNode(this.mSelectedNode);


			if (!aAnimated && aShouldClick)
			{
				//click the node
				if (this.mListener != null)
				{
					this.mListener.mapViewUserDidClickNode(this, this.mNodes.get(mSelectedNode));
				}
				else
				{
					Log.w(MapView.TAG, "Listener is null");
				}
			}
		}
		else
		{
			//tell the view to center on that node
			this.centerOnNode(this.mSelectedNode);

			if (aShouldClick)
			{
				//click the node
				if (this.mListener != null)
				{
					this.mListener.mapViewUserDidClickNode(this, this.mNodes.get(mSelectedNode));
				}
				else
				{
					Log.w(MapView.TAG, "Listener is null");
				}
			}
		}

		this.requestRedraw();
	}

	//calculates the max and min origin bounds
	private void calculateOriginBounds()
	{
		float minX = 0.0f, minY = 0.0f;
		float maxX = 1.0f - this.MAP_ON_SCREEN_WIDTH;
		float maxY = 1.0f - this.MAP_ON_SCREEN_HEIGHT;

		if (maxX < minX)
		{
			maxX = minX;
		}
		if (maxY < minY)
		{
			maxY = minY;
		}

		//set the new bounds
		this.mOriginBounds = new RectF(minX, maxY, maxX, minY);
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
		PointF centered = new PointF(centerOn.getX() - this.MAP_ON_SCREEN_WIDTH/2, centerOn.getY() - this.MAP_ON_SCREEN_HEIGHT/2);
		//set the origin
		this.setOrigin(centered);
	}

	private void startAnimationToNode(MapNode aToNode, boolean aShouldClick)
	{
		this.mSelectedOverlayAnimationPercent = 0.0f;
		this.mSelectedOverlayAnimationStartNode = this.mNodes.get(this.mSelectedNode);
		this.mSelectedOverlayAnimationToNode = aToNode;

		if (this.mSelectedOverlayAnimator == null)
		{
			// Create a new value animator that will use the range 0 to 1
			this.mSelectedOverlayAnimator = ValueAnimator.ofFloat(0, 1);

			// It will take XXXms for the animator to go from 0 to 1
			this.mSelectedOverlayAnimator.setDuration(this.mSelectedOverlayAnimationLength);
			// Callback that executes on animation steps. 
			this.mSelectedOverlayAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					mSelectedOverlayAnimationPercent = ((Float) (animation.getAnimatedValue())).floatValue();
					requestRedraw();
				}
			});

			if (aShouldClick)
			{
				this.mSelectedOverlayAnimator.addListener(new AnimatorListener()
				{

					@Override
					public void onAnimationCancel(Animator arg0)
					{
						// do nothing
					}

					@Override
					public void onAnimationEnd(Animator arg0)
					{
						//click the node
						if (mListener != null)
						{
							mListener.mapViewUserDidClickNode(THIS, mNodes.get(mSelectedNode));
							mSelectedOverlayAnimator = null;
						}
						else
						{
							Log.w(MapView.TAG, "Listener is null");
						}
					}

					@Override
					public void onAnimationRepeat(Animator arg0)
					{
						// do nothing
					}

					@Override
					public void onAnimationStart(Animator arg0)
					{
						// do nothing
					}
				});
			}

			this.mSelectedOverlayAnimator.start();
		}
		else if (this.mSelectedOverlayAnimator.isRunning())
		{
			this.mSelectedOverlayAnimator.cancel();
			this.mSelectedOverlayAnimator = null;
		}
	}

	//overridden view method
	protected void onDraw(Canvas canvas)
	{

		canvas.save();

		this.drawParrallax(canvas);

		canvas.scale(this.mScaleX, this.mScaleY);

		if (this.mStarManager != null)
		{
			this.mStarManager.draw(canvas);
		}

		//draw background
		this.drawBackground(canvas);
		//draw all the nodes on top
		this.drawNodesWithinView(canvas);
		canvas.restore();
	}

	private void drawParrallax(Canvas canvas)
	{
		if (this.mParrallaxImage != null)
		{
			canvas.save();
			canvas.scale(this.mParrallaxScale, this.mParrallaxScale);
			float y = this.mOrigin.y - 1.0f;
			while (y < 1.0f + this.mOrigin.y)
			{
				canvas.drawBitmap(this.mParrallaxImage, 0, y * this.getHeight(), null);
				y += this.MAP_ON_SCREEN_HEIGHT;
			}
			canvas.restore();
		}
	}

	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		if (this.mBackgroundImages != null && mBackgroundLoaded)
		{
			for (int i = this.mBackgroundImages.length - 1; i >= 0; i--)
			{
				float yVal = (1.0f / this.mBackgroundImages.length) * (i + 1);
				if (Math.abs(yVal - this.mOrigin.y) < this.MAP_ON_SCREEN_HEIGHT * 2)
				{
					PointF screen = this.convertToScreenSpace(0.0f, yVal);
					canvas.drawBitmap(this.mBackgroundImages[i], screen.x, screen.y, null);
				}
			}
		}
	}

	//draws all the nodes that are within the bounds of the screen
	private void drawNodesWithinView(Canvas canvas)
	{
		//enumerate through all map nodes
		for (int i = 0; i < this.mNodes.size(); i++)
		{
			MapNode node = this.mNodes.get(i);
			//check if it's on screen in the X direction
			if (Math.abs(node.getX() - this.mOrigin.x) < this.MAP_ON_SCREEN_WIDTH * 1.5f)
			{
				//check if it's on screen in the Y direction
				if (Math.abs(node.getY() - this.mOrigin.y) < this.MAP_ON_SCREEN_HEIGHT * 1.5f)
				{
					//draw it
					PointF screenDrawCenter = this.convertToScreenSpace(node.getX(), node.getY());

					Bitmap onImage = (node.onIcon == null ? this.mNodeImageOn : node.onIcon);
					Bitmap offImage = (node.offIcon == null ? this.mNodeImageOff : node.offIcon);
					float offOffsetX = (node.offIcon == null ? this.mNodeHalfSizeX : node.offIcon.getWidth()/2);
					float offOffsetY = (node.offIcon == null ? this.mNodeHalfSizeY : node.offIcon.getHeight()/2);
					float onOffsetX = (node.onIcon == null ? this.mNodeHalfSizeX : node.onIcon.getWidth()/2);
					float onOffsetY = (node.onIcon == null ? this.mNodeHalfSizeY : node.onIcon.getHeight()/2);
					if (i == this.mSelectedNode)
					{
						canvas.drawBitmap(offImage, screenDrawCenter.x - offOffsetX, screenDrawCenter.y - offOffsetY, this.mNodeOffPaint);
						canvas.drawBitmap(onImage, screenDrawCenter.x - onOffsetX, screenDrawCenter.y - onOffsetY, this.mNodeOnPaint);
					}
					else
					{
						//get which bitmap to use for this node
						boolean state = this.mNodeStates.get(i).booleanValue();
						float offsetX = (state ? onOffsetX : offOffsetX);
						float offsetY = (state ? onOffsetY : offOffsetY);
						Bitmap useToDraw = (state ? onImage : offImage);
						canvas.drawBitmap(useToDraw, screenDrawCenter.x - offsetX, screenDrawCenter.y - offsetY, null);
					}
				}
			}
		}

		if (this.mSelectedNodeOverlayStatic != null && this.mSelectedOverlayAnimationStartNode != null)
		{
			Bitmap drawWith = (this.mSelectedOverlayAnimator == null ? this.mSelectedNodeOverlayStatic : 
				(this.mSelectedNodeOverlayAnimating == null ? this.mSelectedNodeOverlayStatic : this.mSelectedNodeOverlayAnimating));
			//draw the overlay
			float deltaX = (this.mSelectedOverlayAnimationToNode.getX() - this.mSelectedOverlayAnimationStartNode.getX()) * this.mSelectedOverlayAnimationPercent;
			float deltaY = (this.mSelectedOverlayAnimationToNode.getY() - this.mSelectedOverlayAnimationStartNode.getY()) * this.mSelectedOverlayAnimationPercent;

			PointF screenDrawCenter = this.convertToScreenSpace(this.mSelectedOverlayAnimationStartNode.getX() + deltaX, this.mSelectedOverlayAnimationStartNode.getY() + deltaY);
			Matrix matrix = new Matrix();
			//flips the picture if moving in other direction
			/*
			if (this.mSelectedOverlayAnimator != null && deltaX < 0)
			{
				matrix.setScale(-1,1);
				matrix.postTranslate(2 * this.mOverlayHalfSizeX, 0);
			}
			 */
			matrix.postTranslate(screenDrawCenter.x - this.mOverlayHalfSizeX, screenDrawCenter.y - this.mOverlayHalfSizeY);

			canvas.drawBitmap(drawWith, matrix, null);
		}
	}

	//tells the map view to update all of the node�s states and redraw
	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		if (this.mNodeImageOff != null)
		{
			this.mNodeHalfSizeX = (int)(this.mNodeImageOff.getWidth() / 2);
			this.mNodeHalfSizeY = (int)(this.mNodeImageOff.getHeight() / 2);
		}

		if (this.mSelectedNodeOverlayStatic != null)
		{
			this.mOverlayHalfSizeX = (int)(this.mSelectedNodeOverlayStatic.getWidth() / 2.0f);
			this.mOverlayHalfSizeY = (int)(this.mSelectedNodeOverlayStatic.getHeight() / 2.0f);
		}

		if (this.mParrallaxImage != null)
		{
			float scaleX = (w / (float)this.mParrallaxImage.getWidth());
			float scaleY = (h / (float)this.mParrallaxImage.getHeight());
			this.mParrallaxScale = Math.max(scaleX, scaleY);
		}

		this.calculateBackground();
	}

	private void calculateBackground()
	{
		if (this.mBackgroundImages != null && this.mBackgroundImages[0] != null)
		{
			this.mScaleX = this.MAP_ON_SCREEN_WIDTH * (this.getWidth() / (float)this.mBackgroundImages[0].getWidth());
		}
		this.mScaleY = this.mScaleX;
		this.MAP_ON_SCREEN_HEIGHT = (this.getHeight() / (float)this.mBackgroundTotalHeight) / this.mScaleY;

		this.calculateOriginBounds();
	}

	//gets touch events for view
	@SuppressLint("ClickableViewAccessibility")
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

	//handles all touch down events
	private void touchDown(MotionEvent aEvent)
	{
		this.mLastTouchPoint.x = aEvent.getX();
		this.mLastTouchPoint.y = aEvent.getY();
	}

	//handles all touch moved events
	private void touchMoved(MotionEvent aEvent)
	{
		float deltaX = aEvent.getX() - this.mLastTouchPoint.x;
		float deltaY = aEvent.getY() - this.mLastTouchPoint.y;

		//check if we are already scrolling
		if (this.mScrolling)
		{
			//increment the last touch point
			this.mLastTouchPoint.x += deltaX;
			this.mLastTouchPoint.y += deltaY;
			//calculate the delta movement in map space
			float scaledX = (deltaX / this.getWidth()) * this.MAP_ON_SCREEN_WIDTH;
			float scaledY = (deltaY / this.getHeight()) * this.MAP_ON_SCREEN_HEIGHT;
			//increment the origin by the delta
			this.incrementOrigin(-scaledX * MapView.SCROLL_SCALAR, scaledY * MapView.SCROLL_SCALAR);
		}
		else
		{
			//check if we've exceeded the minimum scroll distance
			if (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) >= Math.pow(MapView.mMinScrollDelta, 2))
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
				boolean canClick = false;
				if (this.mListener != null)
				{
					canClick = this.mListener.mapViewUserCanClickNode(this, nodeNearLocation);
				}
				else
				{
					Log.w(MapView.TAG, "Listener is null");
				}

				if (canClick)
				{
					//user can click node
					Log.v(MapView.TAG, "User clicked node with key: " + nodeNearLocation.getLevelKey());
					this.setSelectedNode(this.mNodes.indexOf(nodeNearLocation), true, true);
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
		float scaledX = (aX / this.getWidth()) * this.MAP_ON_SCREEN_WIDTH  + this.mOrigin.x;
		float scaledY = (1.0f - (aY / this.getHeight())) * this.MAP_ON_SCREEN_HEIGHT + this.mOrigin.y;
		return new PointF(scaledX, scaledY);
	}

	private PointF convertToScreenSpace(float aX, float aY)
	{
		float scaledX = (aX - this.mOrigin.x) * this.getWidth() / this.MAP_ON_SCREEN_WIDTH / this.mScaleX;
		float scaledY = (this.MAP_ON_SCREEN_HEIGHT - aY + this.mOrigin.y) * this.getHeight() / this.MAP_ON_SCREEN_HEIGHT / this.mScaleY;

		return new PointF(scaledX, scaledY);
	}

	//find a node near the location. If there isn�t a node, returns null
	private MapNode getNodeNearPoint(PointF aPoint)
	{
		MapNode nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for (MapNode node : this.mNodes)
		{
			double distance = Math.sqrt(Math.pow(aPoint.x - node.getX(), 2) + Math.pow(aPoint.y- node.getY(), 2));
			if (Math.abs(distance) <= this.mMaxNodeClickDistance && nearestDistance > Math.abs(distance))
			{
				nearest = node;
				nearestDistance = Math.abs(distance);
			}
		}

		return nearest;
	}

	//resets all touch variables
	private void resetTouchVariables()
	{
		//reset variables here as needed
		this.mScrolling = false;
		this.mLastTouchPoint = new PointF(-1.0f, -1.0f);
	}

	///-----Star Manager Methods-----
	public boolean starManagerIsPointOnScreen(StarManager aManager, PointF aPoint)
	{
		boolean result = false;
		if (Math.abs(aPoint.x - this.mOrigin.x) < this.MAP_ON_SCREEN_WIDTH * 1.5f)
		{
			//check if it's on screen in the Y direction
			if (Math.abs(aPoint.y - this.mOrigin.y) < this.MAP_ON_SCREEN_HEIGHT * 1.5f)
			{
				result = true;
			}
		}

		return result;
	}

	public PointF starManagerConvertToScreenSpace(StarManager aManager, PointF aPoint)
	{
		return this.convertToScreenSpace(aPoint.x, aPoint.y);
	}

	public void starManagerNeedsRedraw()
	{
		//do nothing
	}

}
