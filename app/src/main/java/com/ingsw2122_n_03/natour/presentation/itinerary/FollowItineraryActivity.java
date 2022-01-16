package com.ingsw2122_n_03.natour.presentation.itinerary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
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


public class FollowItineraryActivity extends AppCompatActivity implements Marker.OnMarkerClickListener, LocationListener {

    private ActivityFollowItineraryBinding binding;

    private Itinerary itinerary;
    private MapView map;
    private RoadManager roadManager;
    private Polyline myPolyline;
    private MyLocationNewOverlay myLocationNewOverlay;
    private Location lastLocation;

    private final ArrayList<GeoPoint> itineraryWaypoints = new ArrayList<>();

    private final ArrayList<Marker> itineraryIndications = new ArrayList<>();
    private final ArrayList<Marker> myRoadIndications = new ArrayList<>();

    private IterController iterController;
    private LocationManager locationManager;

    private boolean wantsRoadsToStart = false;
    private boolean wantsDirections = false;
    private boolean isRoadMade = false;
    private CardView cardView;
    private ProgressBar progressBar;
    private LinearLayout directionsLayout;
    private LinearLayout toStartLayout;

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

        cardView = binding.cardView;
        progressBar = binding.progressBar;

        SwitchMaterial directionsSwitchMaterial = binding.directionsSwitch;
        SwitchMaterial toStartSwitchMaterial = binding.toStartSwitch;

        directionsLayout = binding.directionsLayout;
        toStartLayout = binding.toStartLayout;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        directionsSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(view.getContext(), "Click on the dots for directions", Toast.LENGTH_SHORT).show();
                wantsDirections = true;
                map.getOverlays().addAll(itineraryIndications);
                if(wantsRoadsToStart) map.getOverlays().addAll(myRoadIndications);
            }else{
                wantsDirections = false;
                map.getOverlays().removeAll(itineraryIndications);
                map.getOverlays().removeAll(myRoadIndications);
            }
        });

        toStartSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                makeIndicationToStartingPoint();
                wantsRoadsToStart = true;
            }else{
                wantsRoadsToStart = false;
                map.getOverlays().remove(myPolyline);
                map.getOverlays().removeAll(myRoadIndications);
            }
        });

        setupMap();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        myLocationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        myLocationNewOverlay.enableMyLocation();

        myLocationNewOverlay.runOnFirstFix(() ->  {
            addWayPoints();
            addPointOfInterests();
            makeRoads();
        });

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

        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(itinerary.getStartPoint());

        wayPoints.addAll(itinerary.getWayPoints());

        for(WayPoint wayPoint : wayPoints){

            NaTourMarker marker = new NaTourMarker(map);

            if(wayPoints.indexOf(wayPoint) == 0) {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            }else if(wayPoints.indexOf(wayPoint) == wayPoints.size()-1){
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            }else{
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle, null));
            }

            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setPosition(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));

            map.getOverlays().add(marker);
            map.invalidate();

            NaTourMarker.NaTourGeoPoint naTourWaypoint = marker.new NaTourGeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude());
            this.itineraryWaypoints.add(naTourWaypoint);

        }

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
            map.invalidate();
        }).start();
    }

    private void makeIndicationToStartingPoint(){
        new Thread(()-> {
            ArrayList<GeoPoint> myRoadWaypoints = new ArrayList<>(itineraryWaypoints);
            myRoadWaypoints.add(0, myLocationNewOverlay.getMyLocation());

            Road road = roadManager.getRoad(myRoadWaypoints);
            myPolyline = RoadManager.buildRoadOverlay(road);
            myPolyline.getOutlinePaint().setStrokeWidth(8);

            makeDirections(road, myRoadIndications);

            map.getOverlays().add(myPolyline);
            map.invalidate();

            cardView.post(() -> {
                map.getController().setZoom(16.50);
                map.getController().animateTo(myLocationNewOverlay.getMyLocation());
                if(wantsDirections) map.getOverlays().addAll(myRoadIndications);
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

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.e("test", "test");

        if (!isRoadMade) {
            if (lastLocation == null) {
                lastLocation = location;
            } else {
                progressBar.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
                directionsLayout.setVisibility(View.VISIBLE);
                toStartLayout.setVisibility(View.VISIBLE);
                isRoadMade = true;
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
