package com.beep_boop.Beep.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.game.PlayScreenActivity.NumberOfClicksChangedListener;


public class GoalBar extends View implements NumberOfClicksChangedListener
{
	///-----Interfaces-----
	public interface ClickListener
	{
		public void goalBarUserDidClick(GoalBar aPlayView);
	}

	///-----Members-----
	/** Holds the tag used for logging */
	private static final String TAG = "GoalBar";
	private Bitmap mFromImage, mToImage, mBackgroundImage;
	private String mFromWord, mToWord, mClicksTitle, mClickNumber;
	private float mFromImagePercentX, mFromImagePercentY, mFromImagePercentWidth;
	private float mToImagePercentX, mToImagePercentY, mToImagePercentWidth;
	private float mFromWordPercentX, mFromWordPercentY, mFromWordPercentWidth;
	private float mToWordPercentX, mToWordPercentY, mToWordPercentWidth;
	private float mClickNumberPercentX, mClickNumberPercentY, mClickNumberPercentHeight;
	private float mClickTitlePercentX, mClickTitlePercentY, mClickTitlePercentHeight;
	private PointF mFromWordDraw, mToWordDraw, mClickTitleDraw, mClickNumberDraw;

	private Paint mWordPaint = new Paint();
	private Paint mClickNumberPaint = new Paint(), mClickTitlePaint = new Paint();;
	private float mDefaultTextSize = 50f;

	private Matrix mFromImageMatrix = new Matrix(), mToImageMatrix = new Matrix(), mBackgroundImageMatrix = new Matrix();
	private ClickListener mListener;


	///-----Constructors-----
	public GoalBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GoalBar, 0, 0);
		try
		{
			this.mFromImagePercentX = a.getFloat(R.styleable.GoalBar_fromImagePercentX, 0.2f);
			this.mFromImagePercentY = a.getFloat(R.styleable.GoalBar_fromImagePercentY, 0.4000f);
			this.mToImagePercentX = a.getFloat(R.styleable.GoalBar_toImagePercentX, 0.8f);
			this.mToImagePercentY = a.getFloat(R.styleable.GoalBar_toImagePercentY, 0.4000f);
			this.mFromWordPercentX = a.getFloat(R.styleable.GoalBar_fromWordPercentX, 0.2f);
			this.mFromWordPercentY = a.getFloat(R.styleable.GoalBar_fromWordPercentY, 0.2740f);
			this.mToWordPercentX = a.getFloat(R.styleable.GoalBar_toWordPercentX, 0.8f);
			this.mToWordPercentY = a.getFloat(R.styleable.GoalBar_toWordPercentY, 0.2740f);
			this.mFromImagePercentWidth = a.getFloat(R.styleable.GoalBar_fromImagePercentWidth, 0.1642f);
			this.mToImagePercentWidth = a.getFloat(R.styleable.GoalBar_toImagePercentWidth, 0.1642f);
			this.mFromWordPercentWidth = a.getFloat(R.styleable.GoalBar_fromWordPercentWidth, 0.2000f);
			this.mToWordPercentWidth = a.getFloat(R.styleable.GoalBar_toWordPercentWidth, 0.2000f);

			this.mWordPaint.setColor(a.getColor(R.styleable.GoalBar_textColor, Color.WHITE));
			this.mClickTitlePaint.setColor(a.getColor(R.styleable.GoalBar_textColor, Color.WHITE));
			this.mClickNumberPaint.setColor(a.getColor(R.styleable.GoalBar_textColor, Color.WHITE));

			int backImage = a.getResourceId(R.styleable.GoalBar_backgroundImage, -1);
			if (backImage != -1)
				this.mBackgroundImage = BitmapFactory.decodeResource(getResources(), backImage, null);
			int stringID = a.getResourceId(R.styleable.GoalBar_clickTitle, -1);
			if (stringID != -1)
			{
				this.mClicksTitle = getContext().getString(stringID);
			}
			else
			{
				this.mClicksTitle = "Moves";
			}
			this.mClickTitlePercentX = a.getFloat(R.styleable.GoalBar_clickTitlePercentX, 0.5f);
			this.mClickTitlePercentY = a.getFloat(R.styleable.GoalBar_clickTitlePercentY, 0.35f);
			this.mClickTitlePercentHeight = a.getFloat(R.styleable.GoalBar_clickTitlePercentHeight, 0.15f);
			
			this.mClickNumberPercentX = a.getFloat(R.styleable.GoalBar_clickNumberPercentX, 0.5f);
			this.mClickNumberPercentY = a.getFloat(R.styleable.GoalBar_clickNumberPercentY, 0.75f);
			this.mClickNumberPercentHeight = a.getFloat(R.styleable.GoalBar_clickNumberPercentHeight, 0.3f);
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
		if (!this.isInEditMode())
		{
			this.mWordPaint.setTypeface(MyApplication.MAIN_FONT);
			this.mClickTitlePaint.setTypeface(MyApplication.MAIN_FONT);
			this.mClickNumberPaint.setTypeface(MyApplication.MAIN_FONT);
			this.mWordPaint.setTextAlign(Paint.Align.CENTER);
			this.mClickTitlePaint.setTextAlign(Paint.Align.CENTER);
			this.mClickTitlePaint.setTextSize(100f);
			this.mClickNumberPaint.setTextAlign(Paint.Align.CENTER);
			this.mClickNumberPaint.setTextSize(100f);
		}
	}

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}

	public void destroy()
	{
		if (this.mBackgroundImage != null)
		{
			this.mBackgroundImage.recycle();
			this.mBackgroundImage = null;
		}
		if (this.mFromImage != null)
		{
			this.mFromImage.recycle();
			this.mFromImage = null;
		}
		if (this.mToImage != null)
		{
			this.mToImage.recycle();
			this.mToImage = null;
		}
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
		if (this.mBackgroundImage != null)
			canvas.drawBitmap(this.mBackgroundImage, this.mBackgroundImageMatrix, null);
		if (this.mFromImage != null)
			canvas.drawBitmap(this.mFromImage, this.mFromImageMatrix, null);
		if (this.mToImage != null)
			canvas.drawBitmap(this.mToImage, this.mToImageMatrix, null);
		if (this.mFromWord != null)
			canvas.drawText(this.mFromWord, this.mFromWordDraw.x, this.mFromWordDraw.y, this.mWordPaint);
		if (this.mToWord != null)
			canvas.drawText(this.mToWord, this.mToWordDraw.x, this.mToWordDraw.y, this.mWordPaint);
		if (this.mClicksTitle != null)
			canvas.drawText(this.mClicksTitle, this.mClickTitleDraw.x, this.mClickTitleDraw.y, this.mClickTitlePaint);
		if (this.mClickNumber != null)
			canvas.drawText(this.mClickNumber, this.mClickNumberDraw.x, this.mClickNumberDraw.y, this.mClickNumberPaint);
	}
	public void set(Bitmap aFromImage, Bitmap aToImage, String aFromWord, String aToWord)
	{
		if (aFromImage != null)
		{
			this.mFromImage = aFromImage;
		}
		if (aToImage != null)
		{
			this.mToImage = aToImage;
		}
		if (aFromWord != null)
		{
			this.mFromWord = aFromWord;
		}
		if (aToWord != null)
		{
			this.mToWord = aToWord;
		}

		this.requestRedraw();
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		float aspect = h / (float)w;
		if (this.mBackgroundImage != null)
		{
			float backImageScaleX = this.getWidth() / (float)this.mBackgroundImage.getWidth();
			float backImageScaleY = this.getHeight() / (float)this.mBackgroundImage.getHeight();
			this.mBackgroundImageMatrix.postScale(backImageScaleX, backImageScaleY);
		}

		if (this.mFromImage != null)
		{
			//from image
			float fromImageX = this.mFromImagePercentX - this.mFromImagePercentWidth / 2;
			float fromImageY = this.mFromImagePercentY - this.mFromImagePercentWidth / 2;		
			float fromImageScaleX = (this.getWidth() * this.mFromImagePercentWidth) / this.mFromImage.getWidth();
			float fromImageScaleY = (this.getHeight() * this.mFromImagePercentWidth / aspect) / this.mFromImage.getHeight();
			this.mFromImageMatrix.setScale(fromImageScaleX, fromImageScaleY);
			this.mFromImageMatrix.postTranslate(fromImageX * this.getWidth(), fromImageY * this.getHeight());
		}

		if (this.mToImage != null)
		{
			//to image
			float toImageX = this.mToImagePercentX - this.mToImagePercentWidth / 2;
			float toImageY = this.mToImagePercentY - this.mToImagePercentWidth / 2;
			float toImageScaleX = (this.getWidth() * this.mToImagePercentWidth) / this.mToImage.getWidth();
			float toImageScaleY = (this.getHeight() * this.mToImagePercentWidth / aspect) / this.mToImage.getHeight();
			this.mToImageMatrix.setScale(toImageScaleX, toImageScaleY);
			this.mToImageMatrix.postTranslate(toImageX * this.getWidth(), toImageY * this.getHeight());
		}

		if (this.mFromWord != null && this.mToWord != null)
		{
			//text size
			float fromWordMaxWidth = this.mFromWordPercentWidth * this.getWidth();
			this.mWordPaint.setTextSize(this.mDefaultTextSize);
			float textSizeFrom = this.mWordPaint.getTextSize();
			while (this.mWordPaint.measureText(this.mFromWord) > fromWordMaxWidth)
			{
				textSizeFrom--;
				this.mWordPaint.setTextSize(textSizeFrom);
			}

			float toWordMaxWidth = this.mToWordPercentWidth * this.getWidth();
			this.mWordPaint.setTextSize(this.mDefaultTextSize);
			float textSizeTo = this.mWordPaint.getTextSize();
			while (this.mWordPaint.measureText(this.mToWord) > toWordMaxWidth)
			{
				textSizeTo--;
				this.mWordPaint.setTextSize(textSizeTo);
			}

			float smallestTextSize = (textSizeFrom > textSizeTo ? textSizeTo : textSizeFrom);
			this.mWordPaint.setTextSize(smallestTextSize);
			this.mWordPaint.setTextSize(smallestTextSize);
			this.mWordPaint.setTextSize(smallestTextSize);

			//get bounding boxes
			Rect fromBoundingRect = new Rect();
			this.mWordPaint.getTextBounds(this.mFromWord, 0, this.mFromWord.length(), fromBoundingRect);
			Rect toBoundingRect = new Rect();
			this.mWordPaint.getTextBounds(this.mToWord, 0, this.mToWord.length(), toBoundingRect);

			//from word
			float fromWordX = this.mFromWordPercentX * this.getWidth();
			float fromWordY = this.mFromWordPercentY * this.getHeight();
			this.mFromWordDraw = new PointF(fromWordX, fromWordY);
			//to word
			float toWordX = this.mToWordPercentX * this.getWidth();
			float toWordY = this.mToWordPercentY * this.getHeight();
			this.mToWordDraw = new PointF(toWordX, toWordY);
		}

		if (this.mClicksTitle != null)
		{
			float clickTitleStringX = this.mClickTitlePercentX * this.getWidth();
			float clickTitleStringY = this.mClickTitlePercentY * this.getHeight();
			this.mClickTitleDraw = new PointF(clickTitleStringX, clickTitleStringY);
			float clickTitleMaxHeight = this.mClickTitlePercentHeight * this.getHeight();
			Rect titleBounds = new Rect(0, 0, 1000000, 1000000);
			while (titleBounds.height() > clickTitleMaxHeight)
			{
				this.mClickTitlePaint.getTextBounds(this.mClicksTitle, 0, this.mClicksTitle.length(), titleBounds);
				this.mClickTitlePaint.setTextSize(this.mClickTitlePaint.getTextSize() - 1);
			}
		}

		float clickNumberStringX = this.mClickNumberPercentX * this.getWidth();
		float clickNumberStringY = this.mClickNumberPercentY * this.getHeight();
		this.mClickNumberDraw = new PointF(clickNumberStringX, clickNumberStringY);
		float clickNumberMaxHeight = this.mClickNumberPercentHeight * this.getHeight();
		Rect numberBounds = new Rect(0, 0, 1000000, 1000000);
		while (numberBounds.height() > clickNumberMaxHeight)
		{
			this.mClickNumberPaint.getTextBounds("00", 0, 2, numberBounds);
			this.mClickNumberPaint.setTextSize(this.mClickNumberPaint.getTextSize() - 1);
		}
	}

	@Override
	public void numberOfClicksChanged(int aNumberOfClicks)
	{
		this.mClickNumber = "" + aNumberOfClicks;
		requestRedraw();
	}
}
