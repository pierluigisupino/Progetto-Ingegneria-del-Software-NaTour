package com.ingsw2122_n_03.natour.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.BuildConfig;
import com.ingsw2122_n_03.natour.databinding.Fragment4AddItineraryBinding;
import com.ingsw2122_n_03.natour.infastructure.directions.TaskLoadedCallback;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Extensions;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import com.ingsw2122_n_03.natour.R;

public class AddItineraryFragment4 extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, TaskLoadedCallback {

    private Fragment4AddItineraryBinding binding;
    private FloatingActionButton addGPX;

    private ActivityResultLauncher<Intent> getGPX;
    private AddItineraryActivity addItineraryActivity;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private GoogleMap myGoogleMap;
    private Geocoder geocoder;
    private Polyline polyline;

    private boolean isFollowing = true;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkSettingsAndStartLocationUpdates();
                } else {
                    checkSettingsAndStartLocationUpdates();
                }
            });

    private LocationCallback locationCallback = new LocationCallback() {

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

    public AddItineraryFragment4(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment4AddItineraryBinding.inflate(inflater, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        geocoder = new Geocoder(getContext());
        addGPX = binding.addGPXbutton;

        getGPX = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                String path = data.getData().getPath();

                GPXParser mParser = new GPXParser();
                Gpx parsedGpx = null;

                try {
                    File file = new File(path);
                    InputStream in = new FileInputStream(file);
                    parsedGpx = mParser.parse(in);
                } catch (IOException | XmlPullParserException e) {
                    Log.e("GPX test", e.toString());
                }

                if (parsedGpx != null) {
                    List<Track> tracks = parsedGpx.getTracks();
                    for (int i = 0; i < tracks.size(); i++) {
                        Track track = tracks.get(i);
                        Log.d("GPX test", "track " + i + ":");
                        List<TrackSegment> segments = track.getTrackSegments();
                        for (int j = 0; j < segments.size(); j++) {
                            TrackSegment segment = segments.get(j);
                            Log.d("GPX test", "  segment " + j + ":");
                            for (TrackPoint trackPoint : segment.getTrackPoints()) {
                                String msg = "    point: lat " + trackPoint.getLatitude() + ", lon " + trackPoint.getLongitude() + ", time " + trackPoint.getTime();
                                Extensions ext = trackPoint.getExtensions();
                                Double speed;
                                if (ext != null) {
                                    speed = ext.getSpeed();
                                    msg = msg.concat(", speed " + speed);
                                }
                                Log.d("GPX test", msg);
                            }
                        }
                    }
                } else {
                    Log.e("GPX test", "Error parsing gpx track!");
                }
            }
        });

        addGPX.setOnClickListener(v -> {
            Intent data = new Intent(Intent.ACTION_GET_CONTENT);
            data.addCategory(Intent.CATEGORY_OPENABLE);
            data.setType("*/*");
            Intent intent = Intent.createChooser(data, "Choose a file");
            getGPX.launch(intent);
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Log.e("natour", "test");

        myGoogleMap = googleMap;
        myGoogleMap.setOnMapLongClickListener(this);
        myGoogleMap.setOnMarkerDragListener(this);
        myGoogleMap.setOnMarkerClickListener(this);
        myGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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

        SettingsClient client = LocationServices.getSettingsClient(addItineraryActivity);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> startLocationUpdates());

        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(addItineraryActivity, 10001);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private void showAlertGpsPermissionNeeded(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("Permesso necessario");
        alertDialogBuilder.setMessage("Abbiamo bisogno di accedere alla tua posizone per fornirti il nostro servizio.");
        alertDialogBuilder.setPositiveButton("Apri impostazioni", (dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            getActivity().startActivity(intent);
        });
        alertDialogBuilder.setNegativeButton("Esci", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void askLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(addItineraryActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertGpsPermissionNeeded();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
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
            Log.e("natoure", location.toString());
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