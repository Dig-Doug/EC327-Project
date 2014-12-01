package com.beep_boop.Beep.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.R;

public class GoalBar extends View
{
	///-----Interfaces-----
	public interface ClickListener
	{
		public void goalBarUserDidClick(GoalBar aPlayView);
	}

	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "GoalBar";

	private ClickListener mListener;

	///-----Constructors-----
	public GoalBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayView, 0, 0);
		try
		{
			
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
		
	}

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}

	//sets the listener
	public void setListener(ClickListener aListener)
	{
		this.mListener = aListener;
	}

	///-----Functions-----
	//overridden view method
	@Override
	public void onDraw(Canvas canvas)
	{
		this.drawBackground(canvas);
	}

	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
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
			Log.e(GoalBar.TAG, "Unknown motion event type: " + event.getAction());
			result = false;
		}

		return result;
	}

	//handles all touch down events
	private void touchDown(MotionEvent aEvent)
	{
		
	}

	//handles all touch moved events
	private void touchMoved(MotionEvent aEvent)
	{
		
	}

	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}

	//handles all touch up events
	private void touchUp(MotionEvent aEvent)
	{
		if (this.mListener != null)
		{
			this.mListener.goalBarUserDidClick(this);
		}
	}
}
