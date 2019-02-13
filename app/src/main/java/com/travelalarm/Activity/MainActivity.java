package com.travelalarm.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.travelalarm.Fragment.MapsFragment;
import com.travelalarm.R;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;

    public static int navItemIndex = 0;
    private static final String TAG_MAP = "map";
    private static final String TAG_ALARM = "alarm";
    private static final String TAG_FRIENDS = "friends";
    public static String CURRENT_TAG = TAG_MAP;

    private static final String urlProfileImg = "https://ibb.co/KyNxrrH";

    private String[] activityTitles;

    private boolean shouldLoadMapFragOnBackPress = true;
    private Handler mHandle;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandle = new Handler();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

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

        if(savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAP;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     */
    private void loadNavHeader() {
        txtName.setText("Trung Nghia");

        //load profile image
        Glide.with(this).load(urlProfileImg)
                .thumbnail(0.5f)
                .into(imgProfile);

    }

    /***
     * Return respected fragment that user
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
                        CURRENT_TAG = TAG_ALARM;
                        break;
                    case R.id.nav_friends:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_FRIENDS;
                        break;
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
}
