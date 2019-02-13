package com.travelalarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.travelalarm.Activity.MainActivity;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;
    private String ringstone;

    private ImageView dismiss_image;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ringstone = sharedPreferences.getString("pref_ringstone","");

        try{
            int resId = this.getResources().getIdentifier(ringstone,"raw",this.getPackageName());
            mediaPlayer = MediaPlayer.create(this,resId);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }catch (Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

        String info = getIntent().getStringExtra("info");
        textView = (TextView) findViewById(R.id.info_textview);
        textView.setText(info);

        dismiss_image = (ImageView) findViewById(R.id.dissmiss_image);
        dismiss_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                Intent intent = new Intent(AlarmActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
