package com.beep_boop.Beep.startScreen;

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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;

public class WordDisplay extends View
{

	///-----Members-----
	/** Holds the tag used for logging */
	//private static final String TAG = "WordDisplay";

	private Bitmap mFromImage, mToImage, mArrowImage, mBackgroundImage;
	private String mFromWord, mToWord;
	private float mFromImagePercentX, mFromImagePercentY, mToImagePercentX, mToImagePercentY;
	private float mArrowImagePercentX, mArrowImagePercentY, mFromWordPercentX, mFromWordPercentY, mToWordPercentX, mToWordPercentY;
	private float mFromImagePercentWidth, mToImagePercentWidth;
	private float mArrowImagePercentWidth, mFromWordPercentWidth, mToWordPercentWidth;

	private PointF mFromWordDraw, mToWordDraw;

	private Paint mTextPaint = new Paint();
	private float mDefaultTextSize = 50f;

	private Matrix mFromImageMatrix = new Matrix(), mToImageMatrix = new Matrix(), mArrowImageMatrix = new Matrix(), mBackgroundImageMatrix = new Matrix();;

	///-----Constructors-----
	public WordDisplay(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WordDisplay, 0, 0);
		try
		{
			this.mFromImagePercentX = a.getFloat(R.styleable.WordDisplay_fromImagePercentX, 0.2083f);
			this.mFromImagePercentY = a.getFloat(R.styleable.WordDisplay_fromImagePercentY, 0.4545f);
			this.mToImagePercentX = a.getFloat(R.styleable.WordDisplay_toImagePercentX, 0.7916f);
			this.mToImagePercentY = a.getFloat(R.styleable.WordDisplay_toImagePercentY, 0.4545f);
			this.mArrowImagePercentX = a.getFloat(R.styleable.WordDisplay_arrowImagePercentX, 0.5f);
			this.mArrowImagePercentY = a.getFloat(R.styleable.WordDisplay_arrowImagePercentY, 0.5f);
			this.mFromWordPercentX = a.getFloat(R.styleable.WordDisplay_fromWordPercentX, 0.2083f);
			this.mFromWordPercentY = a.getFloat(R.styleable.WordDisplay_fromWordPercentY, 0.8409f);
			this.mToWordPercentX = a.getFloat(R.styleable.WordDisplay_toWordPercentX, 0.7916f);
			this.mToWordPercentY = a.getFloat(R.styleable.WordDisplay_toWordPercentY, 0.8409f);
			this.mFromImagePercentWidth = a.getFloat(R.styleable.WordDisplay_fromImagePercentWidth, 0.3055f);
			this.mToImagePercentWidth = a.getFloat(R.styleable.WordDisplay_toImagePercentWidth, 0.3055f);
			this.mArrowImagePercentWidth = a.getFloat(R.styleable.WordDisplay_arrowImagePercentWidth, 0.1222f);
			this.mFromWordPercentWidth = a.getFloat(R.styleable.WordDisplay_fromWordPercentWidth, 0.3055f);
			this.mToWordPercentWidth = a.getFloat(R.styleable.WordDisplay_toWordPercentWidth, 0.3055f);
			this.mTextPaint.setColor(a.getColor(R.styleable.WordDisplay_textColor, Color.WHITE));
			int arrowImage = a.getResourceId(R.styleable.WordDisplay_arrowImage, -1);
			if (arrowImage != -1)
				this.mArrowImage = BitmapFactory.decodeResource(getResources(), arrowImage, null);
			int backImage = a.getResourceId(R.styleable.WordDisplay_backgroundImage, -1);
			if (backImage != -1)
				this.mBackgroundImage = BitmapFactory.decodeResource(getResources(), backImage, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			a.recycle();
		}

		if (!this.isInEditMode())
		{
			this.mTextPaint.setTypeface(MyApplication.MAIN_FONT);
		}
	}
	
	public void destroy()
	{
		if (this.mArrowImage != null)
		{
			this.mArrowImage.recycle();
			this.mArrowImage = null;
		}
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

	///-----Functions-----
	//overridden view method
	@Override
	public void onDraw(Canvas canvas)
	{
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

	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}

	@Override
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

