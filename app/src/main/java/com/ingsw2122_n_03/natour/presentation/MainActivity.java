package com.ingsw2122_n_03.natour.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.io.IOException;
import java.util.List;

public class MainActivity extends BaseActivity implements OnMapReadyCallback{

    private AuthController authController;
    private ConstraintLayout layout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private GoogleMap myGoogleMap;

    private boolean isFollowing = true;

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if(isFollowing){
                Location userLocation = locationResult.getLastLocation();
                LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                isFollowing = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = AuthController.getInstance();
        authController.setMainActivity(MainActivity.this);

        layout = findViewById(R.id.layout);
        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.signOut) {
                authController.signOut(MainActivity.this);
                return true;
            }

            return false;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(MainActivity.this, R.color.success))
                .show());
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(MainActivity.this, R.color.error))
                .show());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myGoogleMap = googleMap;
        myGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.map_style));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkSettingsAndStartLocationUpdates();
            myGoogleMap.setMyLocationEnabled(true);
        } else {
            askLocationPermission();
        }
    }

    private void checkSettingsAndStartLocationUpdates(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> startLocationUpdates());

        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(MainActivity.this, 10001);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private void showAlertGpsPermissionNeeded(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Permesso necessario");
        alertDialogBuilder.setMessage("Abbiamo bisogno di accedere alla tua posizone per fornirti il nostro servizio.");
        alertDialogBuilder.setPositiveButton("Apri impostazioni", (dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
            intent.setData(uri);
            MainActivity.this.startActivity(intent);
            finish();
        });
        alertDialogBuilder.setNegativeButton("Esci", (dialogInterface, i) -> finish());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertGpsPermissionNeeded();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10000);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            } else {
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            if (location != null){
                Log.e("natoure", location.toString());
            }else{
                Log.e("natoure", "Null location");
            }
        });

        locationTask.addOnFailureListener(e -> Log.e("natoure", e.getLocalizedMessage()));
    }

}