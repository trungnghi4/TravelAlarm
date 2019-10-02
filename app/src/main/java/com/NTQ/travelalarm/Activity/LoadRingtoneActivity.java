package com.NTQ.travelalarm.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadRingtoneActivity extends BaseActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<String> arrayList;
    ListView listView;
    ArrayAdapter<String> adapter;
    HashMap mapRingtone = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ringtone);

        if(ContextCompat.checkSelfPermission(LoadRingtoneActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(LoadRingtoneActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(LoadRingtoneActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else
            {
                ActivityCompat.requestPermissions(LoadRingtoneActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }else
        {
            doStuff();
        }
    }

    public void doStuff()
    {
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open music player to ....

                Intent intent = getIntent();
                Object Item = listView.getItemAtPosition(position);
                String ringtoneName = (String) Item;
                String ringtonePath =  (String)mapRingtone.get(ringtoneName);
                intent.putExtra("ringtoneName",ringtoneName);
                intent.putExtra("ringtonePath",ringtonePath);
                setResult(1,intent);
                finish();
            }
        });
    }

    public void getMusic()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentLocation = songCursor.getString(songPath);
                mapRingtone.put(currentTitle,currentLocation);
                arrayList.add(currentTitle);

            }while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(LoadRingtoneActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        doStuff();
                    }
                }
                else
                {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("ringtoneName","");
        intent.putExtra("ringtonePath","");
        setResult(1,intent);
        super.onBackPressed();
    }
}
