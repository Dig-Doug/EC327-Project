package com.beep_boop.Beep.game;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class PlayView2 extends View {

	private PointF[] mDrawPoints;
	private int mStartWordIndex = 0;
	private ArrayList<String> mWords;
	private int mNumberofWordsToDraw;
	private float[] mDrawThetas;
	private Paint paint = new Paint();
	
	
	//Goal: plot points 
	public PlayView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mWords = new ArrayList<String>(); 
		this.mWords.add("Apple");
		this.mDrawPoints = new PointF[1];
		this.mDrawPoints[0] = new PointF(200,200);
		this.mDrawThetas = new float[1];
		this.mDrawThetas[0] = 45.0f;

		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {
		
		for (int i = mStartWordIndex; i < mNumberofWordsToDraw; i++)
		{
			float x = mDrawPoints[0].x;
			float y = mDrawPoints[0].y;
			
	        paint.setColor(Color.BLACK);
	        paint.setTextSize(40);
	        String startWord = mWords.get(mStartWordIndex);
	    
	        Rect rect = new Rect();
	        paint.getTextBounds(startWord, 0, startWord.length(), rect);
	        canvas.rotate(mDrawThetas[0], x + rect.exactCenterX(), y + rect.exactCenterY());
			canvas.drawText(startWord, x, y, paint);
		}	
	}
	
	
	//draws the background of the map
	private void drawBackground(Canvas canvas)
	{
		
	}
	//tells the map view to update all of the node�s states and redraw
	private void requestRedraw()
	{
		invalidate();
		requestLayout();
	}
}