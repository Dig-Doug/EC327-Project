package com.beep_boop.Beep.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.MyApplication;
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
	private Bitmap mFromImage, mToImage, mArrowImage, mBackgroundImage;
	private String mFromWord, mToWord;
	private float mFromImagePercentX, mFromImagePercentY, mToImagePercentX, mToImagePercentY;
	private float mArrowImagePercentX, mArrowImagePercentY, mFromWordPercentX, mFromWordPercentY, mToWordPercentX, mToWordPercentY;
	private float mFromImagePercentWidth, mToImagePercentWidth;
	private float mArrowImagePercentWidth, mFromWordPercentWidth, mToWordPercentWidth;

	private PointF mFromWordDraw, mToWordDraw;
	
	private Paint mTextPaint = new Paint();
	private float mDefaultTextSize = 50f;
	
	private Matrix mFromImageMatrix = new Matrix(), mToImageMatrix = new Matrix(), mArrowImageMatrix = new Matrix(), mBackgroundImageMatrix = new Matrix();
	private ClickListener mListener;

	///-----Constructors-----
	public GoalBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayView, 0, 0);
		try
		{
			this.mFromImagePercentX = a.getFloat(R.styleable.WordDisplay_fromImagePercentX, 0.1337f);
			this.mFromImagePercentY = a.getFloat(R.styleable.WordDisplay_fromImagePercentY, 0.4481f);
			this.mToImagePercentX = a.getFloat(R.styleable.WordDisplay_toImagePercentX, 0.8447f);
			this.mToImagePercentY = a.getFloat(R.styleable.WordDisplay_toImagePercentY, 0.4481f);
			this.mArrowImagePercentX = a.getFloat(R.styleable.WordDisplay_arrowImagePercentX, 0.5f);
			this.mArrowImagePercentY = a.getFloat(R.styleable.WordDisplay_arrowImagePercentY, 0.2573f);
			this.mFromWordPercentX = a.getFloat(R.styleable.WordDisplay_fromWordPercentX, 0.3434f);
			this.mFromWordPercentY = a.getFloat(R.styleable.WordDisplay_fromWordPercentY, 0.2573f);
			this.mToWordPercentX = a.getFloat(R.styleable.WordDisplay_toWordPercentX, 0.6058f);
			this.mToWordPercentY = a.getFloat(R.styleable.WordDisplay_toWordPercentY, 0.2573f);
			this.mFromImagePercentWidth = a.getFloat(R.styleable.WordDisplay_fromImagePercentWidth, 0.1642f);
			this.mToImagePercentWidth = a.getFloat(R.styleable.WordDisplay_toImagePercentWidth, 0.1642f);
			this.mArrowImagePercentWidth = a.getFloat(R.styleable.WordDisplay_arrowImagePercentWidth, 0.8724f);
			this.mFromWordPercentWidth = a.getFloat(R.styleable.WordDisplay_fromWordPercentWidth, 0.1818f);
			this.mToWordPercentWidth = a.getFloat(R.styleable.WordDisplay_toWordPercentWidth, 0.1818f);
			this.mTextPaint.setColor(a.getColor(R.styleable.WordDisplay_textColor, Color.WHITE));
			Drawable arrowImage = a.getDrawable(R.styleable.WordDisplay_arrowImage); 
			if (arrowImage != null)
				this.mArrowImage = ((BitmapDrawable) arrowImage).getBitmap();
			Drawable backImage = a.getDrawable(R.styleable.WordDisplay_backgroundImage);
			if (backImage != null)
				this.mBackgroundImage = ((BitmapDrawable) backImage).getBitmap();
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
			this.mTextPaint.setTypeface(MyApplication.MAIN_FONT);
		}
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
		if (this.mBackgroundImage != null)
			canvas.drawBitmap(this.mBackgroundImage, this.mBackgroundImageMatrix, null);
		if (this.mArrowImage != null)
			canvas.drawBitmap(this.mArrowImage, this.mArrowImageMatrix, null);
		if (this.mFromImage != null)
			canvas.drawBitmap(this.mFromImage, this.mFromImageMatrix, null);
		if (this.mToImage != null)
			canvas.drawBitmap(this.mToImage, this.mToImageMatrix, null);
		if (this.mFromWord != null)
			canvas.drawText(this.mFromWord, this.mFromWordDraw.x, this.mFromWordDraw.y, this.mTextPaint);
		if (this.mToWord != null)
			canvas.drawText(this.mToWord, this.mToWordDraw.x, this.mToWordDraw.y, this.mTextPaint);
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
	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		invalidate();
		requestLayout();
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
			float fromImageY = this.mFromImagePercentY - this.mFromImagePercentWidth * aspect;		
			float fromImageScaleX = (this.getWidth() * this.mFromImagePercentWidth) / this.mFromImage.getWidth();
			float fromImageScaleY = (this.getHeight() * this.mFromImagePercentWidth / aspect) / this.mFromImage.getHeight();
			this.mFromImageMatrix.setScale(fromImageScaleX, fromImageScaleY);
			this.mFromImageMatrix.postTranslate(fromImageX * this.getWidth(), fromImageY * this.getHeight());
		}

		if (this.mToImage != null)
		{
			//to image
			float toImageX = this.mToImagePercentX - this.mToImagePercentWidth / 2;
			float toImageY = this.mToImagePercentY - this.mToImagePercentWidth * aspect;
			float toImageScaleX = (this.getWidth() * this.mToImagePercentWidth) / this.mToImage.getWidth();
			float toImageScaleY = (this.getHeight() * this.mToImagePercentWidth / aspect) / this.mToImage.getHeight();
			this.mToImageMatrix.setScale(toImageScaleX, toImageScaleY);
			this.mToImageMatrix.postTranslate(toImageX * this.getWidth(), toImageY * this.getHeight());
		}

		if (this.mArrowImage != null)
		{
			//arrow image
			float arrowImageX = this.mArrowImagePercentX - this.mArrowImagePercentWidth / 2;
			float arrowImageY = this.mArrowImagePercentY - this.mArrowImagePercentWidth * aspect;
			float arrowImageScaleX = (this.getWidth() * this.mArrowImagePercentWidth) / this.mArrowImage.getWidth();
			float arrowImageScaleY =(this.getHeight() * this.mArrowImagePercentWidth / aspect) / this.mArrowImage.getHeight();
			this.mArrowImageMatrix.setScale(arrowImageScaleX, arrowImageScaleY);
			this.mArrowImageMatrix.postTranslate(arrowImageX * this.getWidth(), arrowImageY * this.getHeight());
		}

		if (this.mFromWord != null && this.mToWord != null)
		{
			//text size
			float fromWordMaxWidth = this.mFromWordPercentWidth * this.getWidth();
			this.mTextPaint.setTextSize(this.mDefaultTextSize);
			float textSizeFrom = this.mTextPaint.getTextSize();
			while (this.mTextPaint.measureText(this.mFromWord) > fromWordMaxWidth)
			{
				textSizeFrom--;
				this.mTextPaint.setTextSize(textSizeFrom);
			}

			float toWordMaxWidth = this.mToWordPercentWidth * this.getWidth();
			this.mTextPaint.setTextSize(this.mDefaultTextSize);
			float textSizeTo = this.mTextPaint.getTextSize();
			while (this.mTextPaint.measureText(this.mFromWord) > toWordMaxWidth)
			{
				textSizeTo--;
				this.mTextPaint.setTextSize(textSizeTo);
			}

			float smallestTextSize = (textSizeFrom > textSizeTo ? textSizeTo : textSizeFrom);
			this.mTextPaint.setTextSize(smallestTextSize);

			//get bounding boxes
			Rect fromBoundingRect = new Rect();
			this.mTextPaint.getTextBounds(this.mFromWord, 0, this.mFromWord.length(), fromBoundingRect);
			Rect toBoundingRect = new Rect();
			this.mTextPaint.getTextBounds(this.mToWord, 0, this.mToWord.length(), toBoundingRect);

			//from word
			float fromWordX = this.mFromWordPercentX * this.getWidth() - fromBoundingRect.width()/2;
			float fromWordY = this.mFromWordPercentY * this.getHeight() + fromBoundingRect.height()/2;
			this.mFromWordDraw = new PointF(fromWordX, fromWordY);
			//to word
			float toWordX = this.mToWordPercentX * this.getWidth() - toBoundingRect.width()/2;
			float toWordY = this.mToWordPercentY * this.getHeight() + toBoundingRect.height()/2;
			this.mToWordDraw = new PointF(toWordX, toWordY);
		}
	}

}
