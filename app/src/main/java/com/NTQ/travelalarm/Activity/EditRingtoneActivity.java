package com.NTQ.travelalarm.Activity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.Data.RingtoneModel;
import com.NTQ.travelalarm.Other.RingtoneAdapter;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Utils.CommonUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditRingtoneActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout setRingtoneLayout;
    private LinearLayout customRingtoneLayout;
    private RadioButton rdoCustom;
    private RadioButton rdoRingtone;
    private RadioButton rdoHelloRingtone;
    private TextView txtRingtone;
    @BindView(R.id.tvTitleToolbar)
    TextView tvTitleToolbar;
    @BindView(R.id.rlSelectRingtonePhone)
    RelativeLayout rlSelectRingtonePhone;

    ArrayList<RingtoneModel> ringtoneModels;


    private MediaPlayer mediaPlayer;

    private String mRingtoneName = "";
    private String mRingtonePath = "";
    private String ringtonePath;
    private ListView listRingtone;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_ringtone;
    }

    @Override
    public void addControl() {
    }

    @Override
    public void addEvent() {
        // binding view
        ButterKnife.bind(this);

        ringtoneModels = new ArrayList<>();
        listRingtone = (ListView) findViewById(R.id.listRingtone);
        tvTitleToolbar.setText(getResources().getText(R.string.text_ringtone));
        Intent intent = getIntent();
        mRingtoneName = intent.getStringExtra("ringtoneName");
        mRingtonePath = intent.getStringExtra("ringtonePath");

        setRingtoneLayout = (LinearLayout) findViewById(R.id.set_ringtone_layout);
        customRingtoneLayout = (LinearLayout) findViewById(R.id.ringtone_layout_custom);
        rdoCustom = (RadioButton) findViewById(R.id.rdo_custom_ringtone);
        txtRingtone = (TextView) findViewById(R.id.txt_ringtone_name);
        customRingtoneLayout.setVisibility(View.INVISIBLE);
//
//        if (mRingtoneName.equals(getResources().getText(R.string.ringtone))) {
//            rdoRingtone.setChecked(true);
//        } else if (mRingtoneName.equals(getResources().getText(R.string.hello_ringtone))) {
//            rdoHelloRingtone.setChecked(true);
//        } else {
//            customRingtoneLayout.setVisibility(View.VISIBLE);
//            txtRingtone.setText(mRingtoneName);
//            rdoCustom.setChecked(true);
//        }

        setRingtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRingtoneActivity.this, LoadRingtoneActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        rdoCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoCustom.setChecked(true);
                rdoRingtone.setChecked(false);
                rdoHelloRingtone.setChecked(false);
            }
        });
        rlSelectRingtonePhone.setOnClickListener(this);
//        rdoRingtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rdoCustom.setChecked(false);
//                rdoRingtone.setChecked(true);
//                rdoHelloRingtone.setChecked(false);
//                mRingtoneName = getResources().getText(R.string.ringtone).toString();
//                mRingtonePath = "";
//                ringtonePath = getIntent().getStringExtra("ringtonePath");
//
//                if (!mRingtoneName.equals("")) {
//
//                    try {
//                        mediaPlayer = new MediaPlayer();
//                        mediaPlayer.setDataSource(ringtonePath);
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
////                        ringtone = "ringtone";
////
////                        try {
////                            int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
////                            mediaPlayer = MediaPlayer.create(this, resId);
////                            mediaPlayer.setLooping(true);
////                            mediaPlayer.start();
////                        } catch (Exception ex) {
////                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
////                        }
//                    }
//
////                } else {
////                    ringtone = getIntent().getStringExtra("ringtone");
////
////                    try {
////                        if(ringtone.equals("")) {
////                            ringtone = "ringtone";
////                        }
////                        int resId = this.getResources().getIdentifier(ringtone, "raw", this.getPackageName());
////                        mediaPlayer = MediaPlayer.create(this, resId);
////                        mediaPlayer.setLooping(true);
////                        mediaPlayer.start();
////                    } catch (Exception ex) {
////                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
////                    }
//                }
//            }
//        });
//
//        rdoHelloRingtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rdoCustom.setChecked(false);
//                rdoRingtone.setChecked(false);
//                rdoHelloRingtone.setChecked(true);
//                mRingtoneName = getResources().getText(R.string.hello_ringtone).toString();
//                mRingtonePath = "";
//            }
//        });
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            // ID file ringtone in folder 'raw'.
            if (fields[count].getName().contains("ringtone")) {
                int songId = this.getRawResIdByName(fields[count].getName());
                this.mediaPlayer = MediaPlayer.create(this, songId);
                // Song time span (In milliseconds).
                int duration = this.mediaPlayer.getDuration();

                ringtoneModels.add(new RingtoneModel(fields[count].getName(), this.millisecondsToString(duration)));
            }
        }

        RingtoneAdapter adapterRingtone = new RingtoneAdapter(ringtoneModels, this);

        listRingtone.setAdapter(adapterRingtone);
        listRingtone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                rdoCustom.setChecked(false);
//                rdoRingtone.setChecked(true);
//                rdoHelloRingtone.setChecked(false);
                mediaPlayer.release();
                RingtoneModel ringtoneModel = (RingtoneModel) listRingtone.getItemAtPosition(position);
                String filename = ringtoneModel.getNameRingtone();
                int songId = getRawResIdByName(filename);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);// create's

//                    mediaPlayer = MediaPlayer.create(this, songId);
//                    mediaPlayer.setDataSource(EditAlarmActivity.class,Uri.parse(filename));
//                    mediaPlayer.prepare();
                mediaPlayer.start();

//                RingtoneModel ringtoneModel= ringtoneModels.get(position);
//
//                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("ringtoneName", mRingtoneName);
        intent.putExtra("ringtonePath", mRingtonePath);
        setResult(10, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String ringtoneName = data.getStringExtra("ringtoneName");
            String ringtonePath = data.getStringExtra("ringtonePath");
            if (!ringtoneName.equals("") && !ringtonePath.equals("")) {
                mRingtoneName = ringtoneName;
                mRingtonePath = ringtonePath;
                txtRingtone.setText(mRingtoneName);
                rdoCustom.setChecked(true);
                rdoRingtone.setChecked(false);
                rdoHelloRingtone.setChecked(false);
                customRingtoneLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return CommonUtils.formateMilliSeccond(Long.parseLong(durationStr));
    }

    // Tìm ID của một file nguồn trong thư mục 'raw' theo tên.
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        // Trả về 0 nếu không tìm thấy.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        return resID;
    }


    // Chuyển số lượng milli giây thành một String có ý nghĩa.
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
        return minutes + ":" + seconds;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlSelectRingtonePhone:
                Intent intent = new Intent(EditRingtoneActivity.this, LoadRingtoneActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }
}
