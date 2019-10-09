package com.NTQ.travelalarm;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import com.NTQ.travelalarm.Other.Constants;

public class App extends MultiDexApplication {
    public double currentLocationLat;
    private static Context context;
    public double currentLocationLong;
    public String locationProvider;
    private static App singleton;

    public static Context getContext() {
        return context;
    }


    private static Application application;
    public static App getInstance() {
        return singleton;
    }
    private static Application getApplication(){
        return application;
    }

    public SharedPreferences getSettingPreference() {
        return PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
    }
    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
        context = getApplicationContext();
        singleton = this;
    }

    public String getCurrentLocationLat() {
        return getSettingPreference().getString(Constants.CURRENT_LOCATION_LAT, "");
    }

    public void setCurrentLocationLat(double currentLocationLat) {
        SharedPreferences.Editor editor = getSettingPreference().edit();
        editor.putString(Constants.CURRENT_LOCATION_LAT, String.valueOf(currentLocationLat));
        editor.apply();
        this.currentLocationLat = currentLocationLat;
    }

    public String getCurrentLocationLong() {
        return getSettingPreference().getString(Constants.CURRENT_LOCATION_LAT, "");
    }

    public void setCurrentLocationLong(double currentLocationLong) {
        SharedPreferences.Editor editor = getSettingPreference().edit();
        editor.putString(Constants.CURRENT_LOCATION_LONG, String.valueOf(currentLocationLong));
        editor.apply();
        this.currentLocationLong = currentLocationLong;
    }

    public String getCurrentLocationProvider() {
        return getSettingPreference().getString(Constants.LOCATION_PROVIDER, "");
    }

    public void setCurrentLocationProvider(String locationProvider) {
        SharedPreferences.Editor editor = getSettingPreference().edit();
        editor.putString(Constants.LOCATION_PROVIDER, locationProvider);
        editor.apply();
        this.locationProvider = locationProvider;
    }

}
