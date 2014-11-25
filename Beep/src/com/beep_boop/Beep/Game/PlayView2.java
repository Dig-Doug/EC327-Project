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
	private int mStartWordIndex;
	private ArrayList<String> mWords;
	private int mNumberofWordsToDraw;
	private float[] mDrawThetas;

	//Goal: plot points 
	public PlayView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {

		Paint paint = new Paint();
		
		paint.setColor(Color.RED);
		canvas.drawCircle(40, 50, 25, paint);
		
		paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(120, 50, 25, paint);
        
        
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(200, 50, 25, paint);
        
        int x = 75;
        int y = 185;
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        String rotatedtext = "Apple";
        
        
        
        Rect rect = new Rect();
        paint.getTextBounds(rotatedtext, 0, rotatedtext.length(), rect);
        canvas.translate(x, y);
        paint.setStyle(Paint.Style.FILL);
        
        canvas.drawText("Apple", 0, 0, paint);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, paint);
    
         

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