package com.NTQ.travelalarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    Bundle bundleBaseActivity;
    public enum ENUM_ACTIVITY_STATUS {
        ON_START,
        ON_RESUME,
        ON_PAUSED,
        ON_DESTROY,
        ON_RESTART,
        ON_STOP
    }

    public Bundle getBundleBaseActivity() {
        return bundleBaseActivity;
    }

    public void setBundleBaseActivity(Bundle bundleBaseActivity) {
        this.bundleBaseActivity = bundleBaseActivity;
    }

    public int getLayoutId() {
        /* Should implement at children */
        return -1;
    }

    public void addControl() {

    }

    public void addEvent() {
        /* Should implement at children */
    }

    public void handleFlow(int type) {
        /* Should implement at children */
    }

    public SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleBaseActivity = savedInstanceState;
        /* Do not allow device change to landscape layout */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        addControl();
        addEvent();


        App app = (App) getApplicationContext();
        if (app != null) {
            sharedPreferences = app.getSettingPreference();
        }
    }

    /*
     * Function open back
     */
    public void goBack(boolean isBackFragment) {
        if(isBackFragment){
            onFragmentBackPressed();

        }else {
            finish();
        }
    }

    public void onFragmentBackPressed() {
        // Should implement on children
    }

    /*
     * Flow Activity Life Circle
     */
    @Override
    protected void onStart() {
        super.onStart();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_START.ordinal());
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_RESUME.ordinal());
    }

    @Override
    protected void onPause() {
        super.onPause();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_PAUSED.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_DESTROY.ordinal());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_RESTART.ordinal());
    }

    @Override
    protected void onStop() {
        super.onStop();
        handleFlow(ENUM_ACTIVITY_STATUS.ON_STOP.ordinal());
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void showErrorMessage(int errMessage) {

    }


    @Override
    public void showDialog() {
//        loadingView.showView();
    }

    @Override
    public void dismissDialog() {
//        loadingView.loadedView();
    }

    public Fragment getInstantFragment(Class fragmentClass, Bundle bundle) {
        return Fragment.instantiate(this, fragmentClass.getName(), bundle);
    }
}


