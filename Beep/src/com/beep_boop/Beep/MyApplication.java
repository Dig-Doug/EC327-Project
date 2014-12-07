package com.beep_boop.Beep;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.IBinder;

public class MyApplication extends Application
{
	public boolean pauseVar = false;
	public static Activity activeActivity;
	public static boolean activityStarted = false;
	public interface FontChangeListener
	{
		public void fontDidChange();
	}
	
    private static Context context;
    
    
    public static Typeface MAIN_FONT, PLAY_FONT, SPECIALTY_FONT;
    public static String MAIN_FONT_NAME, PLAY_FONT_NAME, SPECIALTY_FONT_NAME;
    
    private static ArrayList<FontChangeListener> fontChangeListeners = new ArrayList<FontChangeListener>();
   
    
	private static boolean mIsBound = false;
	public static MusicService mServ;
	public static ServiceConnection Scon = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
	     binder) {
		mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
		};
		
		void doBindService(){
	 		bindService(new Intent(context,MusicService.class),
					Scon,Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}

		public static void doUnbindService()
		{
			if(mIsBound)
			{
				context.unbindService(Scon);
	      		mIsBound = false;
			}
		}
	
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        
		//doBindService();
		//Intent music = new Intent();
		//music.setClass(context,MusicService.class);
		//startService(music);
		
        MyApplication.MAIN_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[0];
		MyApplication.MAIN_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.MAIN_FONT_NAME);
		MyApplication.PLAY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[1];
		MyApplication.PLAY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.PLAY_FONT_NAME);
		MyApplication.SPECIALTY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[2];
		MyApplication.SPECIALTY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.SPECIALTY_FONT_NAME);
    }
    @Override
    public void onTerminate(){
    	super.onTerminate();
    	
    		//doUnbindService();
    	
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
    
    public static void pauseSong()
    {
    	//pauseVar = true;
    	if (mServ != null)
    	{
    		mServ.pauseMusic();
    	}
    }
    
    public static void stopSong(){
    	doUnbindService();
    }
    

    public static void playSong()
    {
    	if (mServ != null)
    	{
    		mServ.resumeMusic();
    	}
    }
   /* 
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(!MyApplication.activityStarted){
			doUnbindService();
		}
		
		this.mStarBackground.destroy();
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		mServ.pauseMusic();
		//doUnbindService();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		if(!mIsBound){
		doBindService();
		Intent music = new Intent();
		music.setClass(this,MusicService.class);
		startService(music);
		}
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		mServ.resumeMusic();
		
	}
    
    */
}