package com.beep_boop.Beep;

import java.util.ArrayList;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MyApplication extends Application
{
	/*private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon =new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
	     binder) {
		mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
		};

		void doBindService(){
	 		bindService(new Intent(this,MusicService.class),
					Scon,Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}

		void doUnbindService()
		{
			if(mIsBound)
			{
				unbindService(Scon);
	      		mIsBound = false;
			}
		}
		*/
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
		//doBindService();
		//Intent music = new Intent();
		//music.setClass(this,MusicService.class);
		//startService(music);
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
//    private class startmusic extends AsyncTask
/*
public class MusicService extends Service  implements MediaPlayer.OnErrorListener{

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;

    public MusicService() { }

    public class ServiceBinder extends Binder {
     	 MusicService getService()
    	 {
    		return MusicService.this;
    	 }
    }

    @Override
    public IBinder onBind(Intent arg0){return mBinder;}

    @Override
    public void onCreate (){
	  super.onCreate();

       mPlayer = MediaPlayer.create(this, R.raw.thememain);
       mPlayer.setOnErrorListener(this);

       if(mPlayer!= null)
        {
        	mPlayer.setLooping(true);
        	mPlayer.setVolume(100,100);
        }


        mPlayer.setOnErrorListener(new OnErrorListener() {

	  public boolean onError(MediaPlayer mp, int what, int
          extra){

			onError(mPlayer, what, extra);
			return true;
		}
    	  });
	}

    @Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
         mPlayer.start();
         return START_STICKY;
	}

	public void pauseMusic()
	{
		if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			length=mPlayer.getCurrentPosition();

		}
	}

	public void resumeMusic()
	{
		if(mPlayer.isPlaying()==false)
		{
			mPlayer.seekTo(length);
			mPlayer.start();
		}
	}

	public void stopMusic()
	{
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public void onDestroy ()
	{
		super.onDestroy();
		if(mPlayer != null)
		{
		try{
		 mPlayer.stop();
		 mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {

		Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
		if(mPlayer != null)
		{
			try{
				mPlayer.stop();
				mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
		return false;
	}
}*/
}