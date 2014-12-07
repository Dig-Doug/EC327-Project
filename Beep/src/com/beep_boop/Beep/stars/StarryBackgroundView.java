package com.beep_boop.Beep.stars;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.beep_boop.Beep.R;

public class StarryBackgroundView extends View implements StarManager.ScreenSpaceCoverter
{
	///-----Members-----
	/** Holds the tag used for logging */
	//private static final String TAG = "StarryBackgroundView";

	private float mScaleX = 1.0f, mScaleY = 1.0f;
	/** Hold the image to be drawn in the background */
	private Bitmap mBackgroundImage;
	private Bitmap mForegroundImage;
	private StarManager mStarManager;

	///-----Constructors-----
	public StarryBackgroundView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StarryBackgroundView, 0, 0);
		try
		{
			int backgroundImage = a.getResourceId(R.styleable.StarryBackgroundView_backgroundImage, -1);
			if (backgroundImage != -1)
				this.mBackgroundImage = BitmapFactory.decodeResource(getResources(), backgroundImage, null);
			
			int foregroundImage = a.getResourceId(R.styleable.StarryBackgroundView_foregroundImage, -1);
			if (foregroundImage != -1)
				this.mForegroundImage = BitmapFactory.decodeResource(getResources(), foregroundImage, null);
			
			int starArray = a.getResourceId(R.styleable.StarryBackgroundView_starImages, -1);
			if (starArray != -1)
			{
				TypedArray stars = context.getResources().obtainTypedArray(starArray);

				try
				{
					Bitmap[] starImages = new Bitmap[stars.length()];
					for (int i = 0; i < stars.length(); i++)
					{
						int bitmapID = stars.getResourceId(i, -1);
						if (bitmapID != -1)
						{
							BitmapFactory.Options options = new BitmapFactory.Options();
							starImages[i] = BitmapFactory.decodeResource(getResources(), bitmapID, options);
						}
					}

					int numberOfStars = a.getInteger(R.styleable.StarryBackgroundView_numberOfStars, 10);
					
					this.mStarManager = new StarManager(this, true, numberOfStars, starImages, new PointF(0.0f, 0f), 
							0.25f, 1.0f, 
							-15, 15, 
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
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		if (this.mStarManager != null)
		{
			this.mStarManager.start();
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
	}

	public void destroy()
	{
		if (this.mBackgroundImage != null)
		{
			this.mBackgroundImage.recycle();
			this.mBackgroundImage = null;
		}
		if (this.mForegroundImage != null)
		{
			this.mForegroundImage.recycle();
			this.mForegroundImage = null;
		}
		if (this.mStarManager != null)
		{
			this.mStarManager.destroy();
		}
	}

	///-----Functions-----
	//overridden view method
	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.scale(this.mScaleX, this.mScaleY);	
		if (this.mBackgroundImage != null)
		{
			canvas.drawBitmap(this.mBackgroundImage, 0, 0, null);
		}
		
		if (this.mStarManager != null)
		{
			this.mStarManager.draw(canvas);
		}
		
		if (this.mForegroundImage != null)
		{
			canvas.drawBitmap(this.mForegroundImage, 0, 0, null);
		}
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

		if (this.mBackgroundImage != null)
		{
			this.mScaleX = this.getWidth() / (float)this.mBackgroundImage.getWidth();
			this.mScaleY = this.getHeight() / (float)this.mBackgroundImage.getHeight();
		}

	}

	///-----Star Manager Methods-----
	public boolean starManagerIsPointOnScreen(StarManager aManager, PointF aPoint)
	{
		return true;
	}

	public PointF starManagerConvertToScreenSpace(StarManager aManager, PointF aPoint)
	{
		PointF point = new PointF(aPoint.x, aPoint.y);
		point.x *= this.getWidth() / this.mScaleX;
		point.y *= this.getHeight() / this.mScaleY;
		return point;
	}
	
	public void starManagerNeedsRedraw()
	{
		this.requestRedraw();
	}
}
