package com.beep_boop.Beep.launch;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.beep_boop.Beep.R;

public class LaunchActivity extends Activity 
{
	private ImageView logo_image_view;
	private ImageView text_image_view;
	private LaunchActivity THIS = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		logo_image_view = (ImageView) findViewById(R.id.launchActivity_logoImageView);
		text_image_view = (ImageView) findViewById(R.id.launchActivity_textImageView);


		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.anim_fadein);
		logo_image_view.startAnimation(fadeInAnimation);
		text_image_view.startAnimation(fadeInAnimation);

		fadeInAnimation.setAnimationListener(new Animation.AnimationListener() 
		{

			@Override
			public void onAnimationStart(Animation animation) 
			{
				// do nothing

			}

			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// do nothing, does not repeat

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(THIS, R.animator.anim_fadeout);
				logo_image_view.startAnimation(fadeOutAnimation);
				text_image_view.startAnimation(fadeOutAnimation);

				fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() 
				{

					@Override
					public void onAnimationStart(Animation animation) 
					{
						// do nothing

					}

					@Override
					public void onAnimationRepeat(Animation animation) 
					{
						// do nothing, does not repeat

					}

					@Override
					public void onAnimationEnd(Animation animation)
					{

						logo_image_view.setAlpha(0.0f);
						text_image_view.setAlpha(0.0f);
						//@TODO add transition to map page
						
					}
				});
			}
		});
	}
}
