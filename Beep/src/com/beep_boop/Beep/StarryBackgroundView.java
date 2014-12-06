package com.beep_boop.Beep;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


public class StarryBackgroundView extends View
{
	///-----Interfaces-----
	public interface ClickListener
	{
		public void goalBarUserDidClick(StarryBackgroundView aPlayView);
	}

	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "StarryBackgroundView";

	///-----Constructors-----
	public StarryBackgroundView(Context context, AttributeSet attrs)
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
	
	public void destroy()
	{
		
	}

	///-----Functions-----
	//overridden view method
	@Override
	public void onDraw(Canvas canvas)
	{
		
	}

	

	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

	}
}
