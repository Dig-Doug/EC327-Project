package com.beep_boop.Beep;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.util.LruCache;

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
   
    private static LruCache<String, Bitmap> bitmapCache;
    
	//Intent mMusic;
    public static MediaPlayer mPlayer;
    private static int length = 0;
	/*
    public static boolean musicOn = true;
    
	private static boolean mIsBound = false;
	public static MusicService mServ;
	public ServiceConnection Scon = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder
	     binder) {
		mServ = ((MusicService.ServiceBinder) binder).getService();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
		};
		
		void doBindService(){
	 		bindService(new Intent(context,MusicService.class),
					Scon,BIND_AUTO_CREATE);
			mIsBound = true;
		}

		public void doUnbindService()
		{
			if(mIsBound)
			{
				context.unbindService(Scon);
				
	      		mIsBound = false;
			}
		}
	*/
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        
        
        mPlayer = MediaPlayer.create(this, R.raw.thememain);
        //mPlayer.setOnErrorListener(this);
        if(mPlayer!= null)
        {
        	mPlayer.setLooping(true);
        	mPlayer.setVolume(100,100);
        }
        mPlayer.start();

        mPlayer.setOnErrorListener(new OnErrorListener() {

	  public boolean onError(MediaPlayer mp, int what, int
          extra){

			onError(mPlayer, what, extra);
			return true;
		}
    	  });
        
        //mPlayer.start();

        /*
		doBindService();
		Intent music = new Intent();
		music.setClass(context,MusicService.class);
		mMusic = music;
		startService(music);
		*/
        MyApplication.MAIN_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[0];
		MyApplication.MAIN_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.MAIN_FONT_NAME);
		MyApplication.PLAY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[0];
		MyApplication.PLAY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.PLAY_FONT_NAME);
		MyApplication.SPECIALTY_FONT_NAME = MyApplication.context.getResources().getStringArray(R.array.fonts)[2];
		MyApplication.SPECIALTY_FONT = Typeface.createFromAsset(MyApplication.context.getAssets(), MyApplication.SPECIALTY_FONT_NAME);
		
		setupBitmapCache();
    }
    
    private void setupBitmapCache()
    {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        bitmapCache = new LruCache<String, Bitmap>(cacheSize)
        		{
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
        	bitmapCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return bitmapCache.get(key);
    }
    
    @Override
    public void onTerminate(){
    	super.onTerminate();
    	/*
    	mServ.unbindService(Scon);
    	stopService(mMusic);
    	*/
		if(mPlayer != null)
		{
		try{
		 mPlayer.stop();
		 mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
    	//musicOn = false;
    	//doUnbindService();
		//Intent music = new Intent();
		//music.setClass(context,MusicService.class);
    	//stopService(mMusic);
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
		if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			length=mPlayer.getCurrentPosition();

		}
    	/*
    	//pauseVar = true;
    	if (mServ != null)
    	{
    		if(musicOn){
    			mServ.pauseMusic();
    		}
    	}
    	*/
    }
    
    public static void stopSong(){
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
    }

    
    public static void playSong()
    {
		if(mPlayer.isPlaying()==false)
		{
			mPlayer.seekTo(length);
			mPlayer.start();
		}
    	/*
    	if (mServ != null)
    	{
    		if(musicOn){
    		mServ.resumeMusic();
    		}
    	}
    	*/
    }
    public static void turnOffMusic(){
    	
    	
    	
    	//musicOn = false;
    }

}