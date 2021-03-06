package com.NTQ.travelalarm.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.Fragment.AlarmListFragment;
import com.NTQ.travelalarm.Fragment.AlertsListFragment;
import com.NTQ.travelalarm.Fragment.FriendsListFragment;
import com.NTQ.travelalarm.Fragment.TimeAlarmListFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.NTQ.travelalarm.Fragment.MapsFragment;
import com.NTQ.travelalarm.Fragment.SetTimeAlarmFragment;
import com.NTQ.travelalarm.Other.CircleTransform;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Service.AppService;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends BaseActivity {

    public static TextView notiCounter;
    public static View NotiView;

    public static NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;


    //index to identify current menu item
    public static int navItemIndex = 0;

    //tag used to attach the fragment
    private static final String TAG_MAP = "map";
    private static final String TAG_ALARM = "alarm";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_ALERTS = "alerts";
    private static final String TAG_TIME_ALARM = "time_alarm";
    private static final String TAG_TIME_LIST = "time_list";


    public static String CURRENT_TAG = TAG_MAP;

    //toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    //flag to load map fragment when user presses back key
    private boolean shouldLoadMapFragOnBackPress = true;
    private Handler mHandle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void addControl() {
    }

    @Override
    public void addEvent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandle = new Handler();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.menu_dot, null);
//        notiCounter = (TextView) navigationView.getMenu().findItem(R.id.notification_counter).getActionView();

        //Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.username);
        imgProfile = (ImageView) navHeader.findViewById(R.id.image_profile);

        //load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //load nav menu header data
        loadNavHeader();

        //initializing navigation menu
        setUpNavigationView();

        if(getBundleBaseActivity() == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAP;
            loadHomeFragment();
        }

        Intent intent = new Intent(this, AppService.class);
        startService(intent);
    }

    /***
     * Load navigation menu header information
     */
    private void loadNavHeader() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            txtName.setText(sharedPreferences.getString("name", "User name"));

            //load profile image
            Glide.with(this).load(sharedPreferences.getString("avatarURL", "avatar"))
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(VectorDrawableCompat.create(getResources(), R.drawable.ic_account, null))
                    .into(imgProfile);

            navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);

        }
    }

    /***
     * Return respected fragment which was
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        //select appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        //if user select the current navigation menu again,
        //don't do anything
        //just close the navigation drawer
        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG)
                != null) {
            drawerLayout.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if(mPendingRunnable != null) {
            mHandle.post(mPendingRunnable);
        }

        drawerLayout.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                MapsFragment mapsFragment = new MapsFragment();
                return mapsFragment;
            case 1:
                SetTimeAlarmFragment setTimeAlarmFragment = new SetTimeAlarmFragment();
                return setTimeAlarmFragment;
            case 2:
                AlertsListFragment alertsListFragment = new AlertsListFragment();
                return alertsListFragment;
            case 3:
                AlarmListFragment alarmListFragment = new AlarmListFragment();
                return alarmListFragment;
            case 4:
                TimeAlarmListFragment timeAlarmListFragment=new TimeAlarmListFragment();
                return timeAlarmListFragment;
            case 5:
                FriendsListFragment friendsListFragment = new FriendsListFragment();
                return friendsListFragment;
            default:
                return new MapsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_maps:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAP;
                        break;
                    case R.id.nav_time_alarm:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_TIME_ALARM;
                        break;
                    case R.id.nav_alerts:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_ALERTS;
                        break;
                    case R.id.nav_alarm_list:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_ALARM;
                        break;
                    case R.id.nav_time_list:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_TIME_LIST;
                        break;
                    case R.id.nav_friends:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_FRIENDS;
                        break;
                    case R.id.nav_share:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.app_name));
                        String sAux = "\n" + getResources().getText(R.string.send_message) + "\n\n";
                        sAux = sAux + getResources().getText(R.string.link_app) + " \n\n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Choose one"));
                        return true;
                    case R.id.nav_Logout:
//                        SignIn.disconnectFromFacebook();
//                        FacebookSdk.sdkInitialize(getApplicationContext());
//                        Toast.makeText(getBaseContext(),"Vui lòng đăng nhập lại",Toast.LENGTH_LONG).show();
//                        Intent mainIntent = new Intent(MainActivity.this, SplashScreen.class);
//                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(mainIntent);
                        return true;
                    default:
                        navItemIndex = 0;
                }

                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
            return;
        }

        if(shouldLoadMapFragOnBackPress) {
            if(navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAP;
                loadHomeFragment();
                return;
            }
        }
        //super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public static void UpdateNotiCounter(String count)
    {
        try
        {
            View view = navigationView.getMenu().getItem(3).getActionView();
            notiCounter = (TextView) view.findViewById(R.id.notification_counter);
            notiCounter.setText(count);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
