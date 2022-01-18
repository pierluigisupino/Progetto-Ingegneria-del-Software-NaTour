package com.ingsw2122_n_03.natour.presentation.itinerary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityFollowItineraryBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.support.NaTourMarker;
import com.ingsw2122_n_03.natour.presentation.support.PointOfInterest;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FollowItineraryActivity extends AppCompatActivity implements Marker.OnMarkerClickListener {

    private ActivityFollowItineraryBinding binding;

    private Itinerary itinerary;
    private MapView map;
    private RoadManager roadManager;
    private Polyline myPolyline;
    private MyLocationNewOverlay myLocationNewOverlay;

    private final ArrayList<GeoPoint> itineraryWaypoints = new ArrayList<>();

    private final ArrayList<Marker> itineraryIndications = new ArrayList<>();
    private final ArrayList<Marker> myRoadIndications = new ArrayList<>();

    private IterController iterController;
    private LocationManager locationManager;

    private boolean wantsRoadsToStart = false;
    private boolean wantsDirections = false;
    private boolean isFirstRun = true;

    private ProgressBar progressBar;
    private LinearProgressIndicator bottomProgressBar;
    private CardView cardView;

    private LinearLayout directionsLayout;
    private LinearLayout toStartLayout;

    private SwitchMaterial toStartSwitchMaterial;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkSettingsAndStartLocationUpdates();
                } else {
                    toStartSwitchMaterial.setChecked(false);
                    showAlertGpsPermissionNeeded();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = ActivityFollowItineraryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        iterController = IterController.getInstance();

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("itinerary");

        MaterialToolbar materialToolbar = binding.topAppBar;
        materialToolbar.setTitle(itinerary.getName());

        progressBar = binding.progressBar;
        bottomProgressBar = binding.bottomProgressBar;
        cardView = binding.cardView;

        SwitchMaterial directionsSwitchMaterial = binding.directionsSwitch;
        toStartSwitchMaterial = binding.toStartSwitch;

        directionsLayout = binding.directionsLayout;
        toStartLayout = binding.toStartLayout;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        directionsSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(view.getContext(), getString(R.string.info_directions_text), Toast.LENGTH_SHORT).show();
                wantsDirections = true;
                map.getOverlays().addAll(itineraryIndications);
                if(wantsRoadsToStart) map.getOverlays().addAll(myRoadIndications);
            }else{
                wantsDirections = false;
                map.getOverlays().removeAll(itineraryIndications);
                map.getOverlays().removeAll(myRoadIndications);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        toStartSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                wantsRoadsToStart = true;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    askLocationPermission();
                }else {
                    bottomProgressBar.setVisibility(View.VISIBLE);

                    if(myLocationNewOverlay.getMyLocation() != null){
                        makeIndicationToStartingPoint();
                    }else{
                        myLocationNewOverlay.runOnFirstFix(this::makeIndicationToStartingPoint);
                    }
                }

            }else{
                wantsRoadsToStart = false;
                map.getController().animateTo(itineraryWaypoints.get(0));
                map.getOverlays().remove(myPolyline);
                map.getOverlays().removeAll(myRoadIndications);
                bottomProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        setupMap();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && toStartSwitchMaterial.isChecked()){
            toStartSwitchMaterial.setChecked(false);
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !toStartSwitchMaterial.isChecked() && !isFirstRun){
            toStartSwitchMaterial.setChecked(true);
        }

        isFirstRun = false;

        if(map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(map != null) map.onPause();
    }

    @SuppressLint("MissingPermission")
    private void setupMap() {

        map = binding.map;
        map.setClickable(true);
        map.setMultiTouchControls(true);

        map.getController().setZoom(12.0);
        map.getController().animateTo(new GeoPoint(itinerary.getStartPoint().getLatitude(), itinerary.getStartPoint().getLongitude()));
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        roadManager = new OSRMRoadManager(this, null);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        addWayPoints();
        addPointOfInterests();
        makeRoads();

        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);

        myLocationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        myLocationNewOverlay.enableMyLocation();

        map.getOverlays().add(myLocationNewOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        scaleBarOverlay.setCentred(true);

        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(scaleBarOverlay);
    }


    private void addWayPoints() {

        addMarker(itinerary.getStartPoint(), true);

        ArrayList<WayPoint> mItineraryWaypoints = (ArrayList<WayPoint>) itinerary.getWayPoints();
        for(WayPoint wayPoint : itinerary.getWayPoints()){
            if(itinerary.getWayPoints().indexOf(wayPoint) == mItineraryWaypoints.size() -1)
                addMarker(wayPoint, false);
            else
                itineraryWaypoints.add(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));

        }
    }

    private void addMarker(WayPoint wayPoint, boolean isFirst){
        NaTourMarker marker = new NaTourMarker(map);

        if(isFirst){
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            marker.setTitle(getString(R.string.start_point_text));
        }else {
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            marker.setTitle(getString(R.string.end_point_text));
        }



        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setPosition(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));



        map.getOverlays().add(marker);
        map.invalidate();

        NaTourMarker.NaTourGeoPoint naTourWaypoint = marker.new NaTourGeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude());
        this.itineraryWaypoints.add(naTourWaypoint);
    }

    private void addPointOfInterests(){

        HashMap<byte[], GeoPoint> imagesPosition = iterController.calculatePhotoPosition();

        if(imagesPosition.size() > 0) {
            for (Map.Entry<byte[], GeoPoint> entry : imagesPosition.entrySet()) {

                byte[] imageBytes = entry.getKey();

                BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                PointOfInterest pointOfInterest = new PointOfInterest(map, imageBytes);

                pointOfInterest.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image, null));
                pointOfInterest.setImage(drawable);

                pointOfInterest.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                pointOfInterest.setPosition(entry.getValue());

                pointOfInterest.setOnMarkerClickListener(this);

                map.getOverlays().add(pointOfInterest);
            }
        }
    }

    private void makeRoads(){

        new Thread(()-> {

            Road road = roadManager.getRoad(itineraryWaypoints);
            Polyline polyline = RoadManager.buildRoadOverlay(road);
            polyline.getOutlinePaint().setStrokeWidth(8);

            makeDirections(road, itineraryIndications);
            map.getOverlays().add(polyline);

            cardView.post(() -> {
                progressBar.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);

                directionsLayout.setVisibility(View.VISIBLE);

                if(locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)){
                    toStartLayout.setVisibility(View.VISIBLE);
                }
            });

        }).start();
    }

    private void makeIndicationToStartingPoint(){
        new Thread(()-> {
            ArrayList<GeoPoint> myRoadWaypoints = new ArrayList<>(itineraryWaypoints);

            if(!myRoadWaypoints.contains(myLocationNewOverlay.getMyLocation()))
                myRoadWaypoints.add(0, myLocationNewOverlay.getMyLocation());

            Road road = roadManager.getRoad(myRoadWaypoints);
            myPolyline = RoadManager.buildRoadOverlay(road);
            myPolyline.getOutlinePaint().setStrokeWidth(8);

            if(wantsRoadsToStart) {
                makeDirections(road, myRoadIndications);
                map.getOverlays().add(myPolyline);
            }

            cardView.post(() -> {
                if(wantsRoadsToStart) {
                    map.getController().setZoom(16.50);
                    map.getController().animateTo(myLocationNewOverlay.getMyLocation());
                }

                if(wantsDirections) map.getOverlays().addAll(myRoadIndications);

                bottomProgressBar.setVisibility(View.INVISIBLE);
            });
        }).start();
    }

    public void makeDirections(Road road, ArrayList<Marker> indications){

        indications.clear();

        for (int i = 0; i < road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_directions, null));
            nodeMarker.setTitle("Step "+i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
            indications.add(nodeMarker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        if(marker instanceof PointOfInterest)
            showImage(marker.getImage());

        return true;
    }


    public void showImage(Drawable drawable) {

        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        builder.setOnDismissListener(dialogInterface -> {

        });

        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(drawable);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();

    }

    private void checkSettingsAndStartLocationUpdates(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create()).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            bottomProgressBar.setVisibility(View.VISIBLE);
            myLocationNewOverlay.runOnFirstFix(this::makeIndicationToStartingPoint);
        });

        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(this, 10001);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private void showAlertGpsPermissionNeeded(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.required_permission));
        alertDialogBuilder.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_warning));
        alertDialogBuilder.setMessage(getString(R.string.required_permission_description));
        alertDialogBuilder.setPositiveButton(getString(R.string.open_settings), (dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            this.startActivity(intent);
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                toStartSwitchMaterial.setChecked(false);
                showAlertGpsPermissionNeeded();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }
}
