package com.beep_boop.Beep.game;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class PlayView extends View
{
	///-----Interfaces-----
	public interface WordClickListener
	{
		public void playViewUserDidClickWord(PlayView aPlayView, String aWord);
		public boolean playViewUserCanGoBack(PlayView aPlayView, String aCurrentWord);
		public void playViewUserDidGoBack(PlayView aPlayView);
	}

	public interface WordDataSource
	{
		public List<String> playViewWordsForWord(PlayView aPlayView, String aWord);
		public String playViewPreviousWord(PlayView aPlayView);
	}

	enum AnimationState
	{
		Displaying, //words are in normal positions
		AnimatingIn, //words go towards current word
		AnimatingOut, //words go away from current word
	}

	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "PlayView";

	private WordClickListener mListener;
	private WordDataSource mDataSource;
	
	private ArrayList<String> mWords = new ArrayList<String>();
	private String mCurrentWord, mNextWord;

	/** Hold the image to be drawn in the background */
	private Bitmap mBackgroundImage;
	private float mBackgroundScaleX, mBackgroundScaleY, mBackgroundRotation;
	
	private Paint mTextPaint = new Paint();
	private float mRadius = 0.45f;
	private PointF[] mStartPoints, mDrawPoints;
	private float[] mStartThetas, mDrawThetas;
	private PointF mCurrentWordPosition, mCurrentWordDrawPosition;
	private float mCurrentWordTheta, mCurrentWordDrawTheta;
	private int mStartWordIndex = 0;
	private int mNumberOfWordsToDraw = 10;
	
	private float mAnimationPercent;
	private AnimationState mAnimationState = AnimationState.AnimatingOut;
	private int mAnimationInLength, mAnimationOutLength;

	/** Holds the last registered point of the touch in screen coords */
	private PointF mLastTouchPoint = new PointF();
	private boolean mTouching = false;
	private double mLastTouchTime;
	private float mLastDeltaX, mLastDeltaY;
	
	/** Holds whether or not we are scrolling */
	private boolean mScrolling; 
	/** Holds the minimum distance the finger must move to be considered a scroll */
	private static final float mMinScrollDelta = 5.0f;
	private float mScrollScalar, mScrollAcceleration, mScrollVelocityMinimum, mScrollVelocityScalar;
	private float mScrollVelocity = 0.0f;
	private TimeAnimator mScrollAnimator;
	private float mScrollVelocityMax;
	private float mSwipeVelocityMin;
	
	//--Scroll Bar--
	private Paint mScrollBarPaint = new Paint(), mScrollBarBackgroundPaint = new Paint();
	private float mScrollBarPointRadius = 0.03f, mScrollBarPointOuterRadius = 0.05f, mScrollBarWidth = 0.02f;
	private RectF mScrollBarOuterOval, mScrollBarInnerOval, mScrollBarPointOuterOval, mScrollBarPointInnerOval;

	///-----Constructors-----
	public PlayView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayView, 0, 0);
		try
		{
			mScrollScalar = a.getFloat(R.styleable.PlayView_scrollScalar, 0.005f);
			mScrollAcceleration = a.getFloat(R.styleable.PlayView_scrollAcceleration, 0.90f);
			mScrollVelocityMinimum = a.getFloat(R.styleable.PlayView_scrollVelocityMin, 0.015f);
			mScrollVelocityMax = a.getFloat(R.styleable.PlayView_scrollVelocityMax, 0.15f);
			mScrollVelocityScalar = a.getFloat(R.styleable.PlayView_scrollVelocityScalar, 1f);
			mAnimationInLength = a.getInt(R.styleable.PlayView_animationInLength, 1000);
			mAnimationOutLength = a.getInt(R.styleable.PlayView_animationOutLength, 1000);
			Drawable backgroundImage = a.getDrawable(R.styleable.PlayView_backgroundImage);
			if (backgroundImage != null)
				this.mBackgroundImage = ((BitmapDrawable) backgroundImage).getBitmap();
			this.mTextPaint.setColor(a.getColor(R.styleable.PlayView_textColor, Color.WHITE));
			this.mScrollBarPaint.setColor(a.getColor(R.styleable.PlayView_scrollBarColor, Color.WHITE));
			this.mScrollBarBackgroundPaint.setColor(a.getColor(R.styleable.PlayView_scrollBarBackgroundColor, Color.BLACK));
			this.mSwipeVelocityMin = a.getFloat(R.styleable.PlayView_swipeVelocityMin, 0.10f);
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

		this.startAnimationOut();
	}

	private void init()
	{
		this.mTextPaint.setTextSize(60);

		Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), MyApplication.FONT);
		this.mTextPaint.setTypeface(customFont);

		//does a circle pattern
		ArrayList<PointF> startPoints = new ArrayList<PointF>();
		ArrayList<Float> startThetas = new ArrayList<Float>();
		float delta = (float)(Math.PI * 0.85f) / (this.mNumberOfWordsToDraw + 1);
		float theta = -((float)Math.PI/2 * 0.85f);
		for (int i = 0; i < this.mNumberOfWordsToDraw + 2; i++,  theta += delta)
		{
			startPoints.add(new PointF(this.mRadius * (float)Math.cos(theta), this.mRadius * (float)Math.sin(theta) + 0.5f));
			startThetas.add(theta);
		}
		this.setStarts(new PointF(0.05f, 0.5f), 0, startPoints, startThetas);

		this.mScrollAnimator = new TimeAnimator();
		this.mScrollAnimator.setTimeListener(new TimeAnimator.TimeListener()
		{
			@Override
			public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {

				//check if we're in a state where velocity is allowed
				if (mAnimationState == AnimationState.Displaying)
				{
					int sign = (mScrollVelocity > 0 ? 1 : -1);
					float clamped = (Math.abs(mScrollVelocity) > Math.abs(mScrollVelocityMax) ? sign * mScrollVelocityMax : mScrollVelocity);
					//if so, scroll the amount
					scroll(clamped * deltaTime);
					//decrease the velocity
					mScrollVelocity *= mScrollAcceleration;

					//check if the velocity is less than a threshold
					if (Math.abs(mScrollVelocity) < 0.002)
					{
						//if so, stop velocity
						mScrollVelocity = 0.0f;
					}
					if (mScrollVelocity == 0.0f && !mTouching)
					{
						if (mStartWordIndex < 0)
						{
							scroll(-mScrollVelocityMax/2 * deltaTime);
						}
						else if (mStartWordIndex > mWords.size() - mNumberOfWordsToDraw)
						{
							scroll(mScrollVelocityMax/2 * deltaTime);
						}
					}
				}
				else
				{
					//if not, velocity is 0
					mScrollVelocity = 0.0f;
				}
			}
		});
		this.mScrollAnimator.start();
	}

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();

		//clean up the animator
		this.mScrollAnimator.cancel();
		this.mScrollAnimator = null;
	}

	//sets the listener
	public void setListener(WordClickListener aListener)
	{
		this.mListener = aListener;
	}

	//sets the data source
	public void setDataSource(WordDataSource aDataSource)
	{
		this.mDataSource = aDataSource;
	}

	public void setCurrentWord(String aWord)
	{
		this.mStartWordIndex = 0;
		this.mCurrentWord = aWord;
		this.getWords();
	}

	private void getWords()
	{
		this.mWords.clear();
		if (this.mDataSource != null)
		{
			List<String> words = this.mDataSource.playViewWordsForWord(this, this.mCurrentWord);
			if (words != null)
			{
				this.mWords.addAll(words);
			}
		}
		else
		{
			Log.w(PlayView.TAG, "Data source is null, can't get words for current word");
		}
	}
	
	private void setStarts(PointF aCurrentWordStart, float aCurrentWordTheta, ArrayList<PointF> aPoints, ArrayList<Float> aThetas)
	{
		//set the current word position
		this.mCurrentWordPosition = aCurrentWordStart;
		this.mCurrentWordTheta = aCurrentWordTheta;

		//set the number of words to draw
		this.mNumberOfWordsToDraw = aPoints.size() - 1;

		//initialize arrays
		this.mStartPoints = new PointF[aPoints.size()];
		this.mStartThetas = new float[aThetas.size()];
		this.mDrawPoints = new PointF[aPoints.size()];
		this.mDrawThetas = new float[aThetas.size()];

		//set the start points
		for (int i = 0; i < aPoints.size(); i++)
		{
			this.mStartPoints[i] = aPoints.get(i);
			this.mStartThetas[i] = aThetas.get(i);
		}
	}
	
	///-----Animations-----
	private void startAnimationIn()
	{
		this.mAnimationState = AnimationState.AnimatingIn;

		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		// It will take XXXms for the animator to go from 0 to 1
		animator.setDuration(this.mAnimationInLength);
		// Callback that executes on animation steps. 
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mTextPaint.setAlpha((int)((1.0f - mAnimationPercent) * 255));
				mAnimationPercent = ((Float) (animation.getAnimatedValue())).floatValue();
				requestRedraw();
			}
		});
		animator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationCancel(Animator arg0)
			{ 
				//do nothing 
			}

			@Override
			public void onAnimationEnd(Animator arg0)
			{
				startAnimationOut();
			}

			@Override
			public void onAnimationRepeat(Animator arg0)
			{ 
				//do nothing 
			}

			@Override
			public void onAnimationStart(Animator arg0)
			{ 
				//do nothing 
			}
		});

		animator.start();
		requestRedraw();
	}

	private void startAnimationOut()
	{
		this.mAnimationState = AnimationState.AnimatingOut;
		
		mTextPaint.setAlpha(0);
		mAnimationPercent = 0.0f;
		setCurrentWord(mNextWord);
		mNextWord = null;

		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		// It will take XXXms for the animator to go from 0 to 1
		animator.setDuration(this.mAnimationOutLength);
		// Callback that executes on animation steps. 
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mTextPaint.setAlpha((int)(mAnimationPercent * 255));
				mAnimationPercent = ((Float) (animation.getAnimatedValue())).floatValue();
				requestRedraw();
			}
		});
		animator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationCancel(Animator arg0)
			{ 
				//do nothing 
			}

			@Override
			public void onAnimationEnd(Animator arg0)
			{
				mTextPaint.setAlpha(255);
				mAnimationPercent = 0.0f;
				mAnimationState = AnimationState.Displaying;
				requestRedraw();
			}

			@Override
			public void onAnimationRepeat(Animator arg0)
			{ 
				//do nothing 
			}

			@Override
			public void onAnimationStart(Animator arg0)
			{ 
				//do nothing 
			}
		});

		animator.start();
		requestRedraw();
	}
	
	private void scroll(float aIncrement)
	{
		//only scroll if scrolling is allowed
		if (this.mAnimationState == AnimationState.Displaying)
		{
			//increment the percent
			this.mAnimationPercent += aIncrement;

			//bound the percent
			if (this.mAnimationPercent > 1.0f)
			{
				this.mAnimationPercent = 0;
				this.mStartWordIndex--;
				if (this.mStartWordIndex < -this.mWords.size())
				{
					this.mStartWordIndex = -this.mWords.size();
				}
			}
			else if (this.mAnimationPercent < 0.0f)
			{
				this.mAnimationPercent = 1;
				this.mStartWordIndex++;
				if (this.mStartWordIndex >= this.mWords.size())
				{
					this.mStartWordIndex = this.mWords.size();
				}
			}

			//calculate the draw points
			this.requestRedraw();
		}
	}

	///-----Drawing-----
	//overridden view method
	@Override
	public void onDraw(Canvas canvas)
	{
		this.drawBackground(canvas);

		this.calculateScrollBar();
		this.drawScroll(canvas);

		this.calculateDrawPointsAndThetas();

		if (this.mCurrentWord != null)
		{
			String word = this.mCurrentWord;
			this.drawWord(canvas, word, this.mCurrentWordDrawPosition, this.mCurrentWordDrawTheta, this.getWidth() * (this.mRadius - this.mScrollBarPointOuterRadius) - this.mCurrentWordDrawPosition.x);
		}

		for (int i = 0; i < mNumberOfWordsToDraw; i++)
		{
			if (mStartWordIndex + i < this.mWords.size() && mStartWordIndex + i >= 0)
			{
				String word = this.mWords.get(mStartWordIndex + i);
				this.drawWord(canvas, word, this.mDrawPoints[i], this.mDrawThetas[i], this.getWidth() - this.mDrawPoints[i].x);
			}
			else if (mStartWordIndex + i >= this.mWords.size())
			{
				break;
			}
		}	
	}
	

	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		if (this.mBackgroundImage != null)
		{
			Matrix matrix = new Matrix();
			matrix.postTranslate(-this.mBackgroundImage.getWidth() / 2.0f, -this.mBackgroundImage.getHeight() / 2.0f);
			matrix.postRotate(this.mBackgroundRotation);
			matrix.postScale(this.mBackgroundScaleX, this.mBackgroundScaleY);
			matrix.postTranslate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
			canvas.drawBitmap(this.mBackgroundImage, matrix, null);
		}
	}
	
	private void drawScroll(Canvas canvas)
	{
		if (this.mScrollBarOuterOval != null)
			canvas.drawOval(this.mScrollBarOuterOval, this.mScrollBarPaint);
		if (this.mScrollBarInnerOval != null)
			canvas.drawOval(this.mScrollBarInnerOval, this.mScrollBarBackgroundPaint);
		if (this.mScrollBarPointOuterOval != null)
			canvas.drawOval(this.mScrollBarPointOuterOval, this.mScrollBarBackgroundPaint);
		if (this.mScrollBarPointInnerOval != null)
			canvas.drawOval(this.mScrollBarPointInnerOval, this.mScrollBarPaint);
	}

	private void drawWord(Canvas aCanvas, String aWord, PointF aPosition, float aTheta, float maxWidth)
	{
		float oldTextSize = this.mTextPaint.getTextSize();
		while (this.mTextPaint.measureText(aWord) > maxWidth)
		{
			this.mTextPaint.setTextSize(this.mTextPaint.getTextSize() - 1);
		}

		//Rect rect = new Rect();
		//this.mTextPaint.getTextBounds(aWord, 0, aWord.length(), rect);
		//canvas.rotate(aTheta, aPosition.x + rect.exactCenterX(), aPosition.y + rect.exactCenterY()); //this line was the culprit
		aCanvas.drawText(aWord, aPosition.x, aPosition.y, this.mTextPaint);

		this.mTextPaint.setTextSize(oldTextSize);
	}
	
	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}
	
	///-----Calculations-----
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		this.calculateBackgroundScale();
	}
	
	private void calculateScrollBar()
	{
		this.mScrollBarOuterOval = new RectF(-this.getWidth() * (this.mRadius - mScrollBarPointOuterRadius), 
				(0.5f - (this.mRadius - mScrollBarPointOuterRadius)) * this.getHeight(), 
				this.getWidth() * (this.mRadius - mScrollBarPointOuterRadius), 
				(0.5f + (this.mRadius - mScrollBarPointOuterRadius)) * this.getHeight());
		this.mScrollBarInnerOval = new RectF(-this.getWidth() * ((this.mRadius - mScrollBarPointOuterRadius) - this.mScrollBarWidth),
				 (0.5f - (this.mRadius - mScrollBarPointOuterRadius) + this.mScrollBarWidth) * this.getHeight(), 
				 this.getWidth() *  ((this.mRadius - mScrollBarPointOuterRadius) - this.mScrollBarWidth),  
				 (0.5f + (this.mRadius - mScrollBarPointOuterRadius) - this.mScrollBarWidth) * this.getHeight());

		float percent = this.mStartWordIndex / ((float)this.mWords.size() - this.mNumberOfWordsToDraw);
		float angle = (float)(-Math.PI/2 + Math.PI * percent);
		//bound angle
		if (angle < -Math.PI/2)
		{
			angle = (float)-Math.PI/2;
		}
		else if (angle > Math.PI/2)
		{
			angle = (float)Math.PI/2;
		}
		
		float pointX = (float)(Math.cos(angle) * (this.mRadius - mScrollBarPointOuterRadius - this.mScrollBarWidth/2));
		float pointY = (float)(Math.sin(angle) * (this.mRadius - mScrollBarPointOuterRadius - this.mScrollBarWidth/2)) + 0.5f;
		this.mScrollBarPointOuterOval = new RectF((pointX - this.mScrollBarPointOuterRadius) * this.getWidth(), 
				pointY * this.getHeight() - this.mScrollBarPointOuterRadius * this.getWidth(), 
				(pointX + this.mScrollBarPointOuterRadius) * this.getWidth(), 
				pointY * this.getHeight() + this.mScrollBarPointOuterRadius * this.getWidth());
		this.mScrollBarPointInnerOval = new RectF((pointX - this.mScrollBarPointRadius) * this.getWidth(), 
				pointY * this.getHeight() - this.mScrollBarPointRadius * this.getWidth(), 
				(pointX + this.mScrollBarPointRadius) * this.getWidth(), 
				pointY * this.getHeight() + this.mScrollBarPointRadius * this.getWidth());
	}
	
	private void calculateDrawPointsAndThetas()
	{
		//calculate draw position for current word
		this.mCurrentWordDrawPosition = new PointF(this.mCurrentWordPosition.x * this.getWidth(), this.mCurrentWordPosition.y * this.getHeight());
		this.mCurrentWordDrawTheta = this.mCurrentWordTheta * 180 / (float)Math.PI;

		//enumerate through each point
		for (int i = 0; i < this.mStartPoints.length - 1; i++)
		{
			PointF fromPoint = null, toPoint = null;
			float fromTheta = 0f, toTheta = 0f;
			if (this.mAnimationState == AnimationState.Displaying)
			{
				fromPoint = this.mStartPoints[i];
				fromTheta = this.mStartThetas[i];
				toPoint = this.mStartPoints[i + 1];
				toTheta = this.mStartThetas[i + 1];
			}
			else if (this.mAnimationState == AnimationState.AnimatingIn)
			{
				fromPoint = this.mStartPoints[i];
				fromTheta = this.mStartThetas[i];
				toPoint = this.mCurrentWordPosition;
				toTheta = this.mCurrentWordTheta;
			}
			else if (this.mAnimationState == AnimationState.AnimatingOut)
			{
				fromPoint = this.mCurrentWordPosition;
				fromTheta = this.mCurrentWordTheta;
				toPoint = this.mStartPoints[i];
				toTheta = this.mStartThetas[i];
			}

			//use linear interpolation to get the delta for the animation
			float deltaX = (toPoint.x - fromPoint.x) * this.mAnimationPercent;
			float deltaY = (toPoint.y - fromPoint.y) * this.mAnimationPercent;
			float deltaTheta = (toTheta - fromTheta) * this.mAnimationPercent;

			//set the draw point
			this.mDrawPoints[i] = new PointF(fromPoint.x + deltaX, fromPoint.y + deltaY);
			//times by width and height to get draw points in screen coords instead of percents
			this.mDrawPoints[i].x *= this.getWidth();
			this.mDrawPoints[i].y *= this.getHeight();
			//convert radians to degrees
			this.mDrawThetas[i] = (fromTheta + deltaTheta) * 180 / (float)Math.PI;
		}
	}
	
	private void calculateBackgroundScale()
	{
		if (this.mBackgroundImage != null)
		{
			if (this.getWidth() > this.getHeight())
			{
				if (this.mBackgroundImage.getWidth() > this.mBackgroundImage.getHeight())
				{
					this.mBackgroundRotation = 0f;
					this.mBackgroundScaleX = this.getWidth() / (float)this.mBackgroundImage.getWidth();
					this.mBackgroundScaleY = this.getHeight() / (float)this.mBackgroundImage.getHeight();
				}
				else
				{
					this.mBackgroundRotation = -90f;
					this.mBackgroundScaleX = this.getWidth() / (float)this.mBackgroundImage.getHeight();
					this.mBackgroundScaleY = this.getHeight() / (float)this.mBackgroundImage.getWidth();
				}
			}
			else
			{
				if (this.mBackgroundImage.getWidth() > this.mBackgroundImage.getHeight())
				{
					this.mBackgroundRotation = -90f;
					this.mBackgroundScaleX = this.getWidth() / (float)this.mBackgroundImage.getHeight();
					this.mBackgroundScaleY = this.getHeight() / (float)this.mBackgroundImage.getWidth();
				}
				else
				{
					this.mBackgroundRotation = 0f;
					this.mBackgroundScaleX = this.getWidth() / (float)this.mBackgroundImage.getWidth();
					this.mBackgroundScaleY = this.getHeight() / (float)this.mBackgroundImage.getHeight();
				}
			}
		}
	}
	
	///-----Touch Handling-----
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

		this.mLastTouchTime = System.currentTimeMillis();
		this.mScrollVelocity = 0.0f;

		this.mTouching = true;
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

			//increment the origin by the delta
			this.scroll(deltaY * this.mScrollScalar);
		}
		else
		{
			//check if we've exceeded the minimum scroll distance
			if (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) >= Math.pow(PlayView.mMinScrollDelta, 2))
			{
				//if so, we are scrolling
				this.mScrolling = true;
			}
		}

		this.mLastDeltaX = deltaX;
		this.mLastDeltaY = deltaY;
	}

	//handles all touch up events
	private void touchUp(MotionEvent aEvent)
	{
		if (this.mAnimationState == AnimationState.Displaying)
		{
			//check if we are scrolling
			if (this.mScrolling)
			{
				//calculate the delta time
				double currentTime = System.currentTimeMillis();
				double deltaTime = currentTime - this.mLastTouchTime;
				this.mLastTouchTime = currentTime;
				//set the velocity
				float velocityX = (float)(this.mLastDeltaX / deltaTime);
				float velocityY = (float)(this.mLastDeltaY / deltaTime) * mScrollVelocityScalar;
				if (velocityX > this.mSwipeVelocityMin && velocityX > velocityY)
				{
					boolean canGoBack = this.mListener.playViewUserCanGoBack(this, this.mCurrentWord);
					if (canGoBack)
					{
						this.mListener.playViewUserDidGoBack(this);
						this.mNextWord = this.mDataSource.playViewPreviousWord(this);
						this.startAnimationOut();
					}
				}
				else if (Math.abs(velocityY) > this.mScrollVelocityMinimum)
				{
					this.mScrollVelocity = velocityY;
				}
			}
			else
			{
				String clickedWord = this.nearestWordToClick(new Point((int)this.mLastTouchPoint.x, (int)this.mLastTouchPoint.y));
				if (clickedWord != null)
				{
					if (clickedWord.equals(this.mCurrentWord))
					{
						boolean canGoBack = this.mListener.playViewUserCanGoBack(this, this.mCurrentWord);
						if (canGoBack)
						{
							this.mListener.playViewUserDidGoBack(this);
							this.mNextWord = this.mDataSource.playViewPreviousWord(this);
							this.startAnimationIn();
						}
						else
						{
							//@TODO - play sound
						}
					}
					else
					{
						this.mListener.playViewUserDidClickWord(this, clickedWord);
						this.mNextWord = clickedWord;
						this.startAnimationIn();
					}
				}
			}
		}

		//touch ended, reset all variables
		this.resetTouchVariables();
	}

	//resets all touch variables
	private void resetTouchVariables()
	{
		//reset variables here as needed
		this.mScrolling = false;
		this.mTouching = false;
		this.mLastTouchPoint = new PointF(-1.0f, -1.0f);
	}

	private String nearestWordToClick(Point aPoint)
	{
		String word = null;
		Rect rect = new Rect();
		if (this.mCurrentWord != null)
		{
			this.mTextPaint.getTextBounds(this.mCurrentWord, 0, this.mCurrentWord.length(), rect);
			rect.set((int)this.mCurrentWordDrawPosition.x, (int)this.mCurrentWordDrawPosition.y - rect.height(), 
					(int)this.mCurrentWordDrawPosition.x + rect.width(), (int)this.mCurrentWordDrawPosition.y);
			if (rect.contains(aPoint.x, aPoint.y))
			{
				word = this.mCurrentWord;
			}
		}

		if (word == null)
		{
			for (int i = 0; i < this.mNumberOfWordsToDraw; i++)
			{
				if (mStartWordIndex + i < this.mWords.size() && mStartWordIndex + i >= 0)
				{
					String currentWord = this.mWords.get(this.mStartWordIndex + i);
					this.mTextPaint.getTextBounds(currentWord, 0, currentWord.length(), rect);
					rect.set((int)this.mDrawPoints[i].x, (int)this.mDrawPoints[i].y - rect.height(), 
							(int)this.mDrawPoints[i].x + rect.width(), (int)this.mDrawPoints[i].y);
					if (rect.contains(aPoint.x, aPoint.y))
					{
						word = currentWord;
						break;
					}
				}
				else if (mStartWordIndex + i >= this.mWords.size())
				{
					break;
				}
			}
		}

		return word;
	}
}
