package com.ingsw2122_n_03.natour.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amplifyframework.auth.AuthUser;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.BuildConfig;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.infastructure.directions.FetchURL;
import com.ingsw2122_n_03.natour.infastructure.directions.TaskLoadedCallback;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, TaskLoadedCallback {

    private AuthController authController;
    private ConstraintLayout layout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private GoogleMap myGoogleMap;
    private Geocoder geocoder;
    private Polyline polyline;

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

        geocoder = new Geocoder(this);
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
        myGoogleMap.setOnMapLongClickListener(this);
        myGoogleMap.setOnMarkerDragListener(this);
        myGoogleMap.setOnMarkerClickListener(this);
        myGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.map_style));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkSettingsAndStartLocationUpdates();
            myGoogleMap.setMyLocationEnabled(true);

            LatLng[] waypoints = new LatLng[] {
                    new LatLng(41.87953406823568, 12.484918534755707),
                    new LatLng(41.87858047507068, 12.484844103455544),
                    new LatLng(41.87719074981401, 12.484728768467905),
            };

            MarkerOptions place1 = new MarkerOptions().position(new LatLng(41.880650902452025, 12.485749013721943));
            MarkerOptions place5 = new MarkerOptions().position(new LatLng(41.87727238171806, 12.485873401165009));

            myGoogleMap.addMarker(place1);
            myGoogleMap.addMarker(place5);

            String url = getUrl(place1.getPosition(), place5.getPosition(), "walking", waypoints);
            //new FetchURL(MainActivity.this).execute(url, "walking");

        } else {
            askLocationPermission();
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode, @Nullable LatLng[] waypoints) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        StringBuilder str_waypoints = new StringBuilder("&waypoints=optimize:true");

        if(waypoints != null){
            for(LatLng waypoint : waypoints){
                str_waypoints.append("|").append(waypoint.latitude).append(",").append(waypoint.longitude);
            }
        }

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + str_waypoints + "&" + mode;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + BuildConfig.MAPS_API_KEY;
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
                showAlertGpsPermissionNeeded();
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

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(streetAddress).draggable(true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
               marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.remove();
        return false;
    }

    @Override
    public void onTaskDone(Object... values) {
        if(polyline != null){
            polyline.remove();
        }else{
            polyline = myGoogleMap.addPolyline((PolylineOptions) values[0]);
        }

    }
}