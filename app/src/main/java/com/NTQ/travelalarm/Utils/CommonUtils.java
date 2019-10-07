package com.NTQ.travelalarm.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.NTQ.travelalarm.Fragment.MapsFragment.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;

public class CommonUtils {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }

    //kiem tra dieu kien nguoi dung co dong y cho lay dia diem hien tai hay khong
    //neu cho thi hien thi dia diem do
    public static void askPermissionsAndShowMyLocation(Context context, Activity activity) {
        //ask permission if API >= 23
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};

                //request permission
                ActivityCompat.requestPermissions(activity, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
        }
    }
}
