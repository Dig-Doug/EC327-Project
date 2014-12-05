package com.beep_boop.Beep;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class MyApplication extends Application
{
	public interface FontChangeListener
	{
		public void fontDidChange();
	}
	
    private static Context context;
    
    public static Typeface MAIN_FONT, PLAY_FONT, SPECIALTY_FONT;
    public static String MAIN_FONT_NAME, PLAY_FONT_NAME, SPECIALTY_FONT_NAME;
    
    private static ArrayList<FontChangeListener> fontChangeListeners = new ArrayList<FontChangeListener>();

    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        
        MyApplication.MAIN_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[0];
		MyApplication.MAIN_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.MAIN_FONT_NAME);
		MyApplication.PLAY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[1];
		MyApplication.PLAY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.PLAY_FONT_NAME);
		MyApplication.SPECIALTY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[2];
		MyApplication.SPECIALTY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.SPECIALTY_FONT_NAME);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    
    
    public static void addFontChangeListener(FontChangeListener aListener)
    {
    	if (!MyApplication.fontChangeListeners.contains(aListener))
    	{
    		MyApplication.fontChangeListeners.add(aListener);
    	}
    }
    
    public static void removeFontChangeListener(FontChangeListener aListener)
    {
    	if (MyApplication.fontChangeListeners.contains(aListener))
    	{
    		MyApplication.fontChangeListeners.remove(aListener);
    	}
    }
    
    public static void changeFont(int aPlayFont)
    {
    	if (aPlayFont >= 0)
    	{
    		MyApplication.PLAY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[aPlayFont];
    		MyApplication.PLAY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.PLAY_FONT_NAME);
    	}
    	
    	for (FontChangeListener listener : MyApplication.fontChangeListeners)
    	{
    		listener.fontDidChange();
    	}
    }
}