package com.NTQ.travelalarm.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.NTQ.travelalarm.Activity.AlarmActivity;
import com.NTQ.travelalarm.Activity.MainActivity;
import com.NTQ.travelalarm.Activity.SignIn;
import com.NTQ.travelalarm.App;
import com.NTQ.travelalarm.Data.DatabaseHelper;
import com.NTQ.travelalarm.Data.FirebaseHandle;
import com.NTQ.travelalarm.Data.FriendInfo;
import com.NTQ.travelalarm.Data.Route;
import com.NTQ.travelalarm.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class AppService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    public static List<FriendInfo> friendNear;
    int countNoti = 0;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelper dbHelper = new DatabaseHelper(this);
    private static Location currentLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();

        List<Route> listRouteEnable = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE + " WHERE isEnable = 1");
        if (listRouteEnable.size() > 0) {
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
        }

        FirebaseHandle.getInstance().setStatusChange();

        friendNear = new ArrayList<>();
    }

    private boolean isHasRoute(Route route, List<Route> listRoute) {
        for (Route localRoute : listRoute) {
            if (route.getId() == localRoute.getId())
                return true;
        }
        return false;
    }

    private void syncData() {
        //neu la lan dang nhap dau tien
        if (SignIn.isLoginFirst) {
            List<Route> routeList = FirebaseHandle.getInstance().getListRoute();
            DatabaseHelper dbHelper = new DatabaseHelper(AppService.this);
            dbHelper.deleteAllData();

            if (routeList != null) {
                for (Route route : routeList) {
                    dbHelper.insertRouteWithId(route);
                }
            }
        }

        //neu khong phai dang nhap lan dau, dong bo hoa dư lieu tu may len firebase
        if (!SignIn.isLoginFirst) {
            //cap nhat nhưng bao thuc tu may len firebase
            List<Route> listRoute = dbHelper.getListRoute("SELECT * FROM " + DatabaseHelper.TABLE_ROUTE);

            for (Route route : listRoute) {
                try {
                    FirebaseHandle.getInstance().updateRoute(route);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

//            xoa nhung bao thuc ma trong may da xoa
            List<Route> firebaseRoute = FirebaseHandle.getInstance().getListRoute();
            if (firebaseRoute != null) {
                for (Route route : firebaseRoute) {
                    if (!isHasRoute(route, listRoute))
                        FirebaseHandle.getInstance().removeRoute(String.valueOf(route.getId()));
                }
            }
        }

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        App.getInstance().setCurrentLocationLat(location.getLatitude());
        App.getInstance().setCurrentLocationLong(location.getLongitude());
        App.getInstance().setCurrentLocationProvider(location.getProvider());

        FirebaseHandle.getInstance().updateCurPos(location.getLatitude(), location.getLongitude());
        FirebaseHandle.getInstance().setStatusChange();

        //dong bo hoa du lieu
        syncData();

        List<FriendInfo> friends = FirebaseHandle.getInstance().getListFriends();
        if (friends != null) {
            for (FriendInfo friend : friends) {
                if (isNear(friend, location) && friend.isFollowing() && friend.isNotifying()) {
                    Location friendPos = new Location(LocationManager.GPS_PROVIDER);

                    friendPos.setLatitude(friend.getLatitude());
                    friendPos.setLongitude(friend.getLongitude());

                    if (friend.getRingtoneName().equals("")) {
                        Notification.Builder noti = new Notification.Builder(this)
                                .setSmallIcon(R.drawable.ic_friends_white)
                                .setContentText(location.distanceTo(friendPos) + "m")
                                .setContentTitle(friend.getName() + " đang ở gần bạn");
                        noti.setAutoCancel(true);
                        noti.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, noti.build());

                    } else {
                        Intent intent = new Intent(AppService.this, AlarmActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("info", friend.getName() + " đang ở cách bạn " + location.distanceTo(friendPos) + " m");
                        intent.putExtra("ringtone", friend.getRingtoneName());
                        intent.putExtra("ringtonePath", friend.getRingtonePath());
                        startActivity(intent);
                    }
                    FirebaseHandle.getInstance().setNotifyFriend(friend.getId(), false);

                    countNoti++;
                    friendNear.add(friend);
                }
                if (!isNear(friend, location)) {
                    FirebaseHandle.getInstance().setNotifyFriend(friend.getId(), true);
                }

            }
        }

        try {
            if (countNoti >= 0) {
                MainActivity.notiCounter.setText(String.valueOf(countNoti));
                MainActivity.UpdateNotiCounter((countNoti > 5) ? "5+" : String.valueOf(countNoti));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isNear(FriendInfo friend, Location location) {
        if (friend.getStatus().equals("online")) {
            Location friendPos = new Location(LocationManager.GPS_PROVIDER);

            friendPos.setLatitude(friend.getLatitude());
            friendPos.setLongitude(friend.getLongitude());
            if (location.distanceTo(friendPos) < friend.getMinDis()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onDestroy();
    }

    public static Location getCurrentPosition() {
        String lat = App.getInstance().getCurrentLocationLat();
        String lon = App.getInstance().getCurrentLocationLong();
        Location location = null;
        if (lat.equals("") && lon.equals("")) {
            lat = "10";
            lon = "106";
        }
        if (lon != null) {
            String provider = App.getInstance().getCurrentLocationProvider();
            location = new Location(provider);
            location.setLatitude(Double.parseDouble(lat));
            location.setLongitude(Double.parseDouble(lon));
        }
        return location;
    }
}
