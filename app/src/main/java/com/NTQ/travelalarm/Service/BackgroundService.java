package com.NTQ.travelalarm.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.NTQ.travelalarm.Activity.AlarmActivity;
import com.NTQ.travelalarm.Data.DatabaseHelper;
import com.NTQ.travelalarm.Data.Route;
import com.NTQ.travelalarm.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class BackgroundService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseHelper dbHelper = new DatabaseHelper(this);
    private List<Route> listRoute;

    public static boolean IS_ALARMING = false;

    public BackgroundService(){}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();

        listRoute = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");

        if(listRoute != null && listRoute.size() > 0) {
            Notification.Builder noti = new Notification.Builder(this)
                    .setContentTitle("Alarm Travel")
                    .setContentText(listRoute.get(0).getInfo())
                    .setSmallIcon(R.drawable.logo);

            startForeground(1, noti.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        listRoute = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");

        if(listRoute != null && listRoute.size() > 0) {
            if(!IS_ALARMING) {
                for (int i = 0; i < listRoute.size(); i++) {
                    Location desLocation = new Location(LocationManager.GPS_PROVIDER);
                    desLocation.setLatitude(listRoute.get(i).getLatitude());
                    desLocation.setLongitude(listRoute.get(i).getLongitude());

                    listRoute.get(i).setDistance((double)mLastLocation.distanceTo(desLocation));
                    dbHelper.updateRoute(listRoute.get(i));
//                    FirebaseHandle.getInstance().updateRoute(listRoute.get(i));

                    if (mLastLocation.distanceTo(desLocation) < listRoute.get(i).getMinDistance()) {
                        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                        wakeLock.acquire();

                        listRoute.get(i).setIsEnable(0);
                        dbHelper.updateRoute(listRoute.get(i));

//                        FirebaseHandle.getInstance().updateRoute(listRoute.get(i));

                        Intent intent = new Intent(this, AlarmActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("info", listRoute.get(i).getName());
                        intent.putExtra("ringtone", listRoute.get(i).getRingtone());
                        intent.putExtra("ringtonePath", listRoute.get(i).getRingtonePath());
                        //Toast.makeText(BackgroundService.this, "Path: " + listRoute.get(i).getRingtonePath(), Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        IS_ALARMING = true;
                        break;
                    }
                }
            }
        } else {
            stopSelf();
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());

        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android

        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
    }


}
