package com.beep_boop.Beep.DrawingTest;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class DrawingTest extends View {
private PointF[] mDrawPoints;
private int mStartWordIndex;
private ArrayList<String> mWords;
private int mNumberofWordsToDraw;



	public DrawingTest(Context context, AttributeSet attrs) {
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
