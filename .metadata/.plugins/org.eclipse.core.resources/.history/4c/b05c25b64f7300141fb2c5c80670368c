package com.beep_boop.Beep.Game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.R;

public class PlayView extends View
{
	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "PlayView";
	/** Holds the view origin in map space */
	private PointF mOrigin = new PointF(0.0f, 0.0f);
	/** Holds the minimum distance the finger must move to be considered a scroll */
	private static final float mMinScrollDelta = 5.0f;
	/** Holds the last registered point of the touch in screen coords */
	private PointF mLastTouchPoint = new PointF();
	/** Holds whether or not we are scrolling */
	private boolean mScrolling; 
	/** Hold the image to be drawn in the background */
	private Bitmap mBackgroundImage; // This may need to be broken up into multiple images, in which case an array should be used
	
	private Paint mTextPaint;
	
	///-----Constructors-----
	public PlayView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapView, 0, 0);
		try
		{
			Drawable backgroundImage = a.getDrawable(R.styleable.MapView_backgroundImage);
			this.mBackgroundImage = ((BitmapDrawable) backgroundImage).getBitmap();
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

	private void init()
	{
		this.mTextPaint = new Paint();
		this.mTextPaint.setTextSize(20.0f);
		this.mTextPaint.setColor(0x000000);
	}

	///-----Functions-----
	//sets the origin of the view
	public void setOrigin(PointF aOrigin)
	{
		//set the origin
		this.mOrigin = aOrigin;
		//redraw
		this.requestRedraw();
	}

	//overridden view method
	protected void onDraw(Canvas canvas)
	{
		//draw background
		this.drawBackground(canvas);
	}

	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		if (this.mBackgroundImage != null)
		{
			for (int i = 0; i < getWidth(); i+=this.mBackgroundImage.getWidth())
			{
				for (int j = 0; j < getHeight(); j +=this.mBackgroundImage.getHeight())
				{
					canvas.drawBitmap(this.mBackgroundImage, i, j, null);
				}
			}
		}

		//@TODO
		String text = "x: " + this.mOrigin.x + " y: " + this.mOrigin.y;
		canvas.drawText(text, 0, text.length() - 1, 0, 50, this.mTextPaint);
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
			Log.e(PlayView.TAG, "Unknown motion event type: " + event.getAction());
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

		}

		//touch ended, reset all variables
		this.resetTouchVariables();
	}

	//resets all touch variables
	private void resetTouchVariables()
	{
		//reset variables here as needed
		this.mScrolling = false;
		this.mLastTouchPoint = new PointF(-1.0f, -1.0f);
	}
}
