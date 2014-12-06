package com.beep_boop.Beep.stars;

import android.animation.TimeAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public class StarManager
{
	public interface ScreenSpaceCoverter
	{
		public boolean starManagerIsPointOnScreen(StarManager aManager, PointF aPoint);
		public PointF starManagerConvertToScreenSpace(StarManager aManager, PointF aPoint);
	}

	private ScreenSpaceCoverter mScreenSpaceConverter;

	private Star[] mStars;

	private Bitmap[] mStarImages;
	private float mScaleLowerBound, mScaleUpperBound;

	private float mCreationAngleLowerBound, mCreationAngleUpperBound;
	private float mCreationPositionLowerBound, mCreationPositionUpperBound;
	private float mCreationVelocityLowerBound, mCreationVelocityUpperBound;
	private PointF mGravity;

	private TimeAnimator mStarAnimator;
	
	private RectF mBounds = new RectF(-0.05f, -0.05f, 1.05f, 1.05f);


	public StarManager(ScreenSpaceCoverter aScreenSpaceConverter,
			int aMaxStars, Bitmap[] aStarImages, PointF aGravity,
			float aScaleLowerBound, float aScaleUpperBound, 
			float aAngleLowerBound, float aAngleUpperBound, 
			float aPositionLowerBound, float aPositionUpperBound, 
			float aVelocityLowerBound, float aVelocityUpperBound)
	{
		this.mScreenSpaceConverter = aScreenSpaceConverter;
		this.mStars = new Star[aMaxStars];
		this.mStarImages = aStarImages;
		this.mGravity = aGravity;
		this.mScaleLowerBound = aScaleLowerBound;
		this.mScaleUpperBound = aScaleUpperBound;
		this.mCreationAngleLowerBound = aAngleLowerBound * (float)Math.PI / 180.0f;
		this.mCreationAngleUpperBound = aAngleUpperBound * (float)Math.PI / 180.0f;
		this.mCreationPositionLowerBound = aPositionLowerBound;
		this.mCreationPositionUpperBound = aPositionUpperBound;
		this.mCreationVelocityLowerBound = aVelocityLowerBound;
		this.mCreationVelocityUpperBound = aVelocityUpperBound;
	}

	public void start()
	{
		if (this.mStarAnimator == null)
		{
			this.mStarAnimator = new TimeAnimator();
			this.mStarAnimator.setTimeListener(new TimeAnimator.TimeListener()
			{
				@Override
				public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime)
				{
					updateStars(deltaTime/1000.0f);
				}
			});
			this.mStarAnimator.start();
		}
		else
		{
			this.mStarAnimator.start();
		}
	}

	public void pause()
	{
		this.mStarAnimator.pause();
	}

	public void destroy()
	{
		if (mStarAnimator != null)
		{
			this.mStarAnimator.cancel();
			this.mStarAnimator = null;
		}

		for (int i = 0; i < this.mStarImages.length; i++)
		{
			if (this.mStarImages[i] != null)
			{
				this.mStarImages[i].recycle();
				this.mStarImages[i] = null;
			}
		}
	}
	
	private Star createNewStar(Star oldStar)
	{
		Star newStar = oldStar;
		if (newStar == null)
		{
			newStar = new Star();
		}
		
		float randAngle = (float)(Math.random()) * (this.mCreationAngleUpperBound - this.mCreationAngleLowerBound) + this.mCreationAngleLowerBound;
		float xVelocity = (float)Math.cos(randAngle);
		float yVelocity = (float)Math.sin(randAngle);
		float speed = (float)(Math.random()) * (this.mCreationVelocityUpperBound - this.mCreationVelocityLowerBound) + this.mCreationVelocityLowerBound;
		newStar.velocity.x = xVelocity * speed;
		newStar.velocity.y = yVelocity * speed;
		
		if (xVelocity > 0.0f)
		{
			newStar.location.x = this.mBounds.left;
		}
		else
		{
			newStar.location.x = this.mBounds.right * 0.99f;
		}
		newStar.location.y = (float)(Math.random()) * (this.mCreationPositionUpperBound - this.mCreationPositionLowerBound) + this.mCreationPositionLowerBound;
		
		newStar.imageIndex = (int)(this.mStarImages.length * Math.random());
		newStar.scale = (float)(Math.random()) * (this.mScaleUpperBound - this.mScaleLowerBound) + this.mScaleLowerBound;
		
		return newStar;
	}
	
	private void updateStars(double aDelta)
	{
		for (int i = 0; i < this.mStars.length; i++)
		{
			Star star = this.mStars[i];
			if (star != null)
			{				
				if (this.mBounds.contains(star.location.x, star.location.y))
				{
					star.location.x += star.velocity.x * aDelta;
					star.location.y += star.velocity.y * aDelta;
					star.velocity.x += this.mGravity.x * aDelta;
					star.velocity.y += this.mGravity.y * aDelta;
				}
				else
				{
					this.mStars[i] = createNewStar(this.mStars[i]);
				}
			}
			else
			{
				this.mStars[i] = createNewStar(null);
			}
		}
	}

	public void draw(Canvas aCanvas)
	{
		for (Star star : this.mStars)
		{
			if (star != null)
			{
				if (this.mScreenSpaceConverter.starManagerIsPointOnScreen(this, star.location))
				{
					PointF screenDraw = this.mScreenSpaceConverter.starManagerConvertToScreenSpace(this, star.location);
					aCanvas.save();
					aCanvas.translate(screenDraw.x, screenDraw.y);
					aCanvas.scale(star.scale, star.scale);
					aCanvas.drawBitmap(this.mStarImages[star.imageIndex], 0, 0, null);
					aCanvas.restore();
				}
			}
		}
	}
}
