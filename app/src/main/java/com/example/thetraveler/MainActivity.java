package com.example.thetraveler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.thetraveler.webservice.NearbyPlacesService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_ID = 1;
    FusedLocationProviderClient mFusedLocationClient;
    String lat, lng;
    TextView radius;
    Button btnRestaurants, btnLodging, btnMuseums, btnAmusementParks, btnBowlingAlleys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radius = findViewById(R.id.range);
        btnRestaurants = findViewById(R.id.btnRestaurants);
        btnLodging = findViewById(R.id.btnLodging);
        btnMuseums = findViewById(R.id.btnMuseums);
        btnAmusementParks = findViewById(R.id.btnAmusementParks);
        btnBowlingAlleys = findViewById(R.id.btnBowlingAlleys);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        setBtnClickListeners();
    }

    private void setBtnClickListeners() {
        btnRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGetNearbyPlaces("restaurant");
            }
        });

        btnLodging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGetNearbyPlaces("lodging");
            }
        });

        btnMuseums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGetNearbyPlaces("museum");
            }
        });

        btnAmusementParks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGetNearbyPlaces("amusement_park");
            }
        });

        btnBowlingAlleys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGetNearbyPlaces("bowling_alley");
            }
        });
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
                getLastLocation();
            }
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = String.valueOf(location.getLatitude());
                                    lng = String.valueOf(location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = String.valueOf(mLastLocation.getLatitude());
            lng = String.valueOf(mLastLocation.getLongitude());
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
        IntentFilter nearbyPlacesFilter = new IntentFilter(NearbyPlacesService.BROADCAST_NEARBY_PLACES);
        LocalBroadcastManager.getInstance(this).registerReceiver(nearbyPlacesReceiver, nearbyPlacesFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(nearbyPlacesReceiver);
    }

    private String getRadiusMeters() {
        double miles = Double.parseDouble(radius.getText().toString());
        int meters = (int) (miles/.00062137);
        return String.valueOf(meters);
    }

    private void startGetNearbyPlaces(String type) {
        if (lat != null && lat.length() > 0 && lng != null && lng.length() > 0) {
            String meterRadius = getRadiusMeters();
            int milesRadius = Integer.parseInt(radius.getText().toString());
            if (meterRadius.length() > 0 && milesRadius > 0 && milesRadius <= 30) {
                NearbyPlacesService.startGetNearbyPlaces(this, "p1", meterRadius, type, lat, lng);
            } else {
                Toast.makeText(this, "Invalid Range", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Could not retieve location.", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver nearbyPlacesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String key = bundle.getString("KEY");
            String type = bundle.getString("TYPE");
            String results =  bundle.getString("RESULTS");

            if (key.equals("p1")) {
                Intent i = new Intent(context, NearbyPlaces.class);
                i.putExtra("RESULTS", results);
                i.putExtra("TYPE", type);
                startActivity(i);
            }
        }
    };
}
