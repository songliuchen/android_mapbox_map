package tszs.map.mapbox.application;


import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

import tszs.map.mapbox.R;

public class TApplication extends Application

{
    public void onCreate()
    {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }
}
