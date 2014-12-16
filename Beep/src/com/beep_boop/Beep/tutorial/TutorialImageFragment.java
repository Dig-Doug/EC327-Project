package com.beep_boop.Beep.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beep_boop.Beep.R;

public class TutorialImageFragment extends Fragment
{
	public interface ClickHandler
	{
		public void fragmentWasClicked();
	}
	
	private int mResId;
	private ClickHandler mClickHandler;
	
	public TutorialImageFragment(int aResId, ClickHandler aClickHandler)
	{
		mResId = aResId;
		mClickHandler = aClickHandler;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial_screen, container, false);

        ImageView image = (ImageView) rootView.findViewById(R.id.fragmentTutorialScreen_imageView);
        image.setImageResource(mResId);
        
        image.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				mClickHandler.fragmentWasClicked();
			}
        	
        });
        
        return rootView;
    }
}