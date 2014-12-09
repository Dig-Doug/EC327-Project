package com.beep_boop.Beep.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beep_boop.Beep.R;

public class TutorialImageFragment extends Fragment
{
	private int mResId;
	
	public TutorialImageFragment(int aResId)
	{
		mResId = aResId;
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial_screen, container, false);

        ImageView image = (ImageView) rootView.findViewById(R.id.fragmentTutorialScreen_imageView);
        image.setImageResource(mResId);
        
        return rootView;
    }
}