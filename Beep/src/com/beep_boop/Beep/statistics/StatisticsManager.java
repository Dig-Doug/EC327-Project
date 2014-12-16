package com.beep_boop.Beep.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.beep_boop.Beep.MyApplication;
import com.beep_boop.Beep.levels.Level;

public class StatisticsManager 
{
	private static final String TAG = "StatisticsManager";

	private static final String PREFS_NAME = "STATISTICS_MANAGER_PREFS";

	private static final String SAVED_USER_ID_KEY = "SAVED_USER_ID_KEY";
	private static final String SAVED_LEVELS_KEY = "SAVED_LEVELS_KEY";


	private static final String USER_ID_URL = "http://www.roeper.com/test/Beep/beepGetUserID.php";
	private static final String LEVEL_URL = "http://www.roeper.com/test/Beep/beepSaveLevelTime.php";
	private static final String LEVEL_URL_USERNAME = "username";
	private static final String LEVEL_URL_LEVEL = "level";
	private static final String LEVEL_URL_FROM = "from";
	private static final String LEVEL_URL_TO = "to";
	private static final String LEVEL_URL_STATE = "state";
	private static final String LEVEL_URL_TIME = "time";
	private static final String LEVEL_URL_PATH = "path";
	private static final String LEVEL_URL_COUNTRY = "country";

	private static String USER_ID;
	private static final String RANDOM_KEY = "RANDOM";

	public static StatisticsManager INSTANCE = new StatisticsManager();

	private StatisticsManager()
	{
		SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
		if (settings.contains(SAVED_USER_ID_KEY))
		{
			USER_ID = settings.getString(SAVED_USER_ID_KEY, null);
			Log.v(TAG, "Has user id: " + USER_ID);
		}
		else
		{
			new GetUserIDTask().execute();
		}

	}

	public static void recordLevel(Level aLevel, String[] aPath)
	{
		recordData(aLevel.levelKey, aLevel.fromWord, aLevel.toWord, aPath, aLevel.time, true);
	}

	public static void recordRandom(String aFromWord, String aToWord, String[] aPath, double aTime)
	{
		recordData(RANDOM_KEY, aFromWord, aToWord, aPath, aTime, true);
	}

	public static void recordLevelLost(Level aLevel, String[] aPath, double aTime)
	{
		recordData(aLevel.levelKey, aLevel.fromWord, aLevel.toWord, aPath, aTime, false);
	}

	public static void recordRandomLost(String aFromWord, String aToWord, String[] aPath, double aTime)
	{
		recordData(RANDOM_KEY, aFromWord, aToWord, aPath, aTime, false);
	}

	public static void recordData(String aLevelKey, String aFromWord, String aToWord, String[] aPath, double aTime, boolean aWin)
	{
		ConnectivityManager connMgr = (ConnectivityManager)MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
		{
			new PostProgressTask().execute(getURLForParams(aLevelKey, aFromWord, aToWord, aPath, aTime, aWin));

			SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
			if (settings.contains(SAVED_LEVELS_KEY))
			{
				Log.v(TAG, "Posting saved level data");
				new PostSavedLevelTask().execute();
			}
		}
		else
		{
			SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
			Set<String> saved = null;
			if (settings.contains(SAVED_LEVELS_KEY))
			{
				saved = settings.getStringSet(SAVED_LEVELS_KEY, null);
			}

			if (saved == null)
			{
				saved = new HashSet<String>();
			}

			String data = getURLForParams(aLevelKey, aFromWord, aToWord, aPath, aTime, aWin);
			Log.v(TAG, "Saving level data: " + data);
			saved.add(data);

			SharedPreferences.Editor editor = settings.edit();
			editor.putStringSet(SAVED_LEVELS_KEY, saved);
			editor.commit();
		}
	}

	private static String getURLForParams(String aLevelKey, String aFromWord, String aToWord, String[] aPath, double aTime, boolean aWin)
	{
		String countryCode = MyApplication.getAppContext().getResources().getConfiguration().locale.getCountry();

		String path = "";
		if (aPath != null)
		{
			for (String current : aPath)
			{
				path += current + ",";
			}
		}

		String result = LEVEL_URL + "?";
		result += "&" + LEVEL_URL_USERNAME + "=" + USER_ID;
		result += "&" + LEVEL_URL_COUNTRY + "=" + countryCode;
		result += "&" + LEVEL_URL_LEVEL + "=" + (aLevelKey == null ? "" : aLevelKey.replaceAll(" ", "_"));
		result += "&" + LEVEL_URL_FROM + "=" + (aFromWord == null ? "" : aFromWord.replaceAll(" ", "_"));
		result += "&" + LEVEL_URL_TO + "=" + (aToWord == null ? "" : aToWord.replaceAll(" ", "_"));
		result += "&" + LEVEL_URL_STATE + "=" + (aWin ? "WIN" : "LOSE");
		result += "&" + LEVEL_URL_TIME + "=" + aTime;
		result += "&" + LEVEL_URL_PATH + "=" + (path == null ? "" : path.replaceAll(" ", "_"));

		return result;
	}

	private static class GetUserIDTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			if (USER_ID == null)
			{
				try
				{
					String result = connectToUrl(USER_ID_URL);

					if (result != null && USER_ID == null)
					{
						USER_ID = result;

						Log.v(TAG, "Got user id: " + USER_ID);

						SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(SAVED_USER_ID_KEY, USER_ID);
						editor.commit();
					}
				}
				catch (IOException e)
				{
					return null;
				}
			}


			if (USER_ID != null)
			{
				try
				{
					connectToUrl(urls[0]);
					return USER_ID;
				}
				catch (IOException e)
				{
					return null;
				}
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result)
		{

		}
	}

	private static class PostProgressTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			if (USER_ID == null)
			{
				new GetUserIDTask().execute(urls);
			}
			else
			{
				try
				{
					Log.d(TAG, "Posting progess as: " + urls[0]);

					connectToUrl(urls[0]);
				}
				catch (IOException e)
				{
					return "Unable to retrieve web page. URL may be invalid.";
				}
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result)
		{

		}
	}

	private static class PostSavedLevelTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... voids)
		{
			if (USER_ID == null)
			{
				new GetUserIDTask().execute();
			}
			else
			{
				SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
				if (settings.contains(SAVED_LEVELS_KEY))
				{
					Set<String> saved = settings.getStringSet(SAVED_LEVELS_KEY, null);

					if (saved != null)
					{
						for (String current : saved)
						{
							try
							{
								Log.d(TAG, "Posting progess: " + current);

								connectToUrl(current);
							}
							catch (IOException e)
							{
								//error
							}
						}
					}

					SharedPreferences.Editor editor = settings.edit();
					editor.putStringSet(SAVED_LEVELS_KEY, null);
					editor.commit();
				}
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result)
		{

		}
	}

	private static String connectToUrl(String aURL) throws IOException
	{
		InputStream is = null;
		try
		{
			URL url = new URL(aURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			//int response = conn.getResponseCode();
			//Log.d(DEBUG_TAG, "The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String contentAsString = readIt(is, 8);

			is.close();
			conn.disconnect();

			return contentAsString;
		} 
		finally 
		{
			if (is != null) 
			{
				is.close();
			} 
		}

	}

	public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
	{
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");        
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
}
