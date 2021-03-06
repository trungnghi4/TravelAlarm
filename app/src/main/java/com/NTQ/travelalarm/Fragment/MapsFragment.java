package com.NTQ.travelalarm.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.NTQ.travelalarm.Activity.SetAlarmActivity;
import com.NTQ.travelalarm.Data.FirebaseHandle;
import com.NTQ.travelalarm.Data.FriendInfo;
import com.NTQ.travelalarm.Other.MapsHandle;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Service.AppService;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.NTQ.travelalarm.Other.Constants.ONLINE;

import com.google.android.libraries.places.compat.Place;


public class MapsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap mMap;
    private Context context;

    private MapsHandle mapsHandle;

    private Location mCurrentDestination;
    private String mDestinationInfo = "";
    private double currentDistance;

    private FloatingActionButton fabSetAlarm;

    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        context = rootView.getContext();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                onMyMapReady(mMap);
            }
        });

        fabSetAlarm = (FloatingActionButton) rootView.findViewById(R.id.fab_set_alarm);

        fabSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentDestination != null) {
                    Intent intent = new Intent(context, SetAlarmActivity.class);
                    intent.putExtra("latitude", mCurrentDestination.getLatitude());
                    intent.putExtra("longitude", mCurrentDestination.getLongitude());
                    intent.putExtra("des_info", mDestinationInfo);
                    intent.putExtra("cur_dis", currentDistance);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.warning_set_location),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    //su kien khi map da load xong
    //thiet dat cac thong so va su kien click tren ban do de hien thong tin diem da click
    private void onMyMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapsHandle = MapsHandle.getInstance(context, mMap);
        mapsHandle.setMyLocationEnable(true);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //show current location
                mapsHandle.setMyLocationEnable(true);
                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();

            }
        });

        //lay thong tin cua diem vua click
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mDestinationInfo = mapsHandle.getPlaceInfo(latLng);
                addDestinationMarker(latLng);
            }
        });

        mapsHandle.setting();
    }

    //kiem tra dieu kien nguoi dung co dong y cho lay dia diem hien tai hay khong
    //neu cho thi hien thi dia diem do
    private void askPermissionsAndShowMyLocation() {
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
                    ActivityCompat.requestPermissions(getActivity(), permissions,
                            REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                    return;
                }
            }

        //show current location
        showMyLocation();
        mapsHandle.setMyLocationEnable(true);
    }

    //tra ve ket qua nguoi dung co cho phep lay vi tri hay khong
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                //user allow permission
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_LONG).show();
                    this.showMyLocation();
                    mapsHandle.setMyLocationEnable(true);
                }
                //user don't allow
                else {
                    Toast.makeText(context, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    //hien thi dia diem hien tai khi moi vua load xong ban do
    private void showMyLocation() {
        if (AppService.getCurrentPosition() != null) {
            LatLng latLng = new LatLng(AppService.getCurrentPosition().getLatitude(), AppService.getCurrentPosition().getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        } else {
            showSettingsAlert();
        }
    }


    //gan marker cho diem den va ve duong noi va tinh khoang cach giua diem hien tai va diem den
    private void addDestinationMarker(LatLng latLng) {
        //add marker for destination
        mapsHandle.addMarker(latLng, mDestinationInfo);

        //draw path between current location to search location
        Location searchLocation = new Location(LocationManager.GPS_PROVIDER);
        searchLocation.setLatitude(latLng.latitude);
        searchLocation.setLongitude(latLng.longitude);

        if (AppService.getCurrentPosition() != null) {
            List<Location> list = new ArrayList<>();
            list.add(AppService.getCurrentPosition());
            list.add(searchLocation);

            currentDistance = AppService.getCurrentPosition().distanceTo(searchLocation);
            mCurrentDestination = searchLocation;
        } else {
            Toast.makeText(context, getResources().getText(R.string.error_get_current_location),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search)
            onMapSearch();
        if (id == R.id.action_show_friends)
            showFriends();
        return super.onOptionsItemSelected(item);
    }

    //hien thi cong cu search autocomplete
    private void onMapSearch() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("VN").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    //sau khi search tra ve ket qua gan marker cho diem do

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                LatLng latLng = place.getLatLng();
                mDestinationInfo = place.getName().toString();

                addDestinationMarker(latLng);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(context, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
//
    public void showFriends() {
        List<FriendInfo> friends = FirebaseHandle.getInstance().getListFriends();
        final MarkerOptions markerOptions = new MarkerOptions();
        int friendShowed = 0;
        if(friends != null) {
            for (final FriendInfo friend : friends) {
                if (friend.isFollowing() && friend.getStatus().equals(ONLINE)) {
                    friendShowed++;
                    markerOptions.position(new LatLng(friend.getLatitude(), friend.getLongitude()));
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mMap.addMarker(markerOptions);
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {

                                Bitmap bitmap = Glide.with(context)
                                        .load(friend.getAvatarURL())
                                        .asBitmap()
                                        .into(-1, -1).get();

                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(circleBitmap(bitmap)));

                            } catch (Exception ex) {
                                Log.e("LocationFriendActivity", ex.getMessage());
                            }
                            return null;
                        }
                    }.execute();
                }
            }
        }

        if(friendShowed == 0) {
            Toast.makeText(context, "Bạn bè mà bạn theo dõi hiện đã offline hoặc bạn không theo dõi bạn bè nào",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap circleBitmap(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap circleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle((float) width / 2,
                (float) height / 2,
                (float) Math.min(width, height / 2),
                Path.Direction.CCW);

        final Canvas canvas = new Canvas(circleBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return circleBitmap;
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getResources().getText(R.string.setting_title));
        alertDialog.setMessage(context.getResources().getText(R.string.setting_message));

        alertDialog.setPositiveButton(context.getResources().getText(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
