package com.beep_boop.Beep.tutorial;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.R;
import com.beep_boop.Beep.statistics.StatisticsManager;
import com.beep_boop.Beep.tutorial.TutorialImageFragment.ClickHandler;

public class TutorialActivity extends FragmentActivity implements ClickHandler
{
	private TutorialActivity THIS = this;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	boolean activityStarted = false;
	private int[] mScreenIds;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		
		StatisticsManager.recordData("TUTORIAL", null, null, null, -1, true);
		
		MyApplication.playSong();

		TypedArray tutScreens = getResources().obtainTypedArray(R.array.tutorialImages);
		try
		{
			this.mScreenIds = new int[tutScreens.length()];
			for (int i = 0; i < tutScreens.length(); i++)
			{
				int bitmapID = tutScreens.getResourceId(i, -1);
				mScreenIds[i] = bitmapID;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tutScreens.recycle();
		}

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.tutorialActivity_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		ImageButton backButton = (ImageButton) findViewById(R.id.tutorialActivity_backButton);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activityStarted = true;
				finish();
				overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		if (mPager.getCurrentItem() == 0)
		{
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
			activityStarted = true;
			finish();
			overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
		}
		else
		{
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(!activityStarted){
			MyApplication.pauseSong();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	@Override
	protected void onRestart()
	{
		super.onRestart();
		MyApplication.playSong();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		MyApplication.playSong();
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
	{
		public ScreenSlidePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			return new TutorialImageFragment(mScreenIds[position], THIS);
		}

		@Override
		public int getCount()
		{
			return mScreenIds.length;
		}
	}
	
	public void fragmentWasClicked()
	{
		if (mPager.getCurrentItem() == mScreenIds.length - 1)
		{
			activityStarted = true;
			finish();
			overridePendingTransition(R.animator.anim_activity_bottom_in, R.animator.anim_activity_bottom_out);
		}
		else
		{
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
		}
	}
}
