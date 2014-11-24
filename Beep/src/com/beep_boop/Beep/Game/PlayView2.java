package com.beep_boop.Beep.game;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beep_boop.Beep.R;


public class PlayView2 extends View {

	private PointF[] mDrawPoints;
	private int mStartWordIndex;
	private ArrayList<String> mWords;
	private int mNumberofWordsToDraw;
	private float[] mDrawThetas;


	public PlayView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {

		Paint paint = new Paint();
		
		paint.setColor(Color.RED);
		canvas.drawCircle(20, 50, 25, paint);
		
		paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(100, 50, 25, paint);
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