package com.ingsw2122_n_03.natour.presentation.itinerary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.location.LocationListenerCompat;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityFollowItineraryBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.support.NaTourMarker;
import com.ingsw2122_n_03.natour.presentation.support.PointOfInterest;

import org.osmdroid.api.IMapController;
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
    private IMapController mapController;
    private RoadManager roadManager;
    private Polyline roadOverlay;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private MyLocationNewOverlay oMapLocationOverlay;
    private double myLatitude;
    private double myLongitude;
    private GeoPoint myGeoPoint;
    private LocationManager locationManager;
    private Location lastLocation;

    private final ArrayList<GeoPoint> waypoints = new ArrayList<>();
    private final ArrayList<Marker> roadMarkers = new ArrayList<>();

    private IterController iterController;

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

        materialToolbar.setNavigationOnClickListener(v -> finish());

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

    /************
     * MAP UTILS
     ************/

    @SuppressLint("MissingPermission")
    private void setupMap() {

        map = binding.map;
        map.setClickable(true);
        map.setMultiTouchControls(true);

        map.getController().setZoom(20.0);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);

        roadManager = new OSRMRoadManager(this, null);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        gpsMyLocationProvider = new GpsMyLocationProvider(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        oMapLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        oMapLocationOverlay.enableFollowLocation();
        oMapLocationOverlay.enableMyLocation();

        oMapLocationOverlay.runOnFirstFix(() ->  {
            addWayPoints();
            addPointOfInterests();
            makeRoads();
        });

        map.getOverlays().add(oMapLocationOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);
    }


    private void addWayPoints() {

        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(itinerary.getStartPoint());
        if(itinerary.getWayPoints() != null)
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
            this.waypoints.add(naTourWaypoint);

        }

    }

    private void addPointOfInterests(){

        HashMap<byte[], GeoPoint> imagesPosition = iterController.calculatePhotoPosition();

        for(Map.Entry<byte[], GeoPoint> entry : imagesPosition.entrySet()){

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

    private void makeRoads(){

        new Thread(()-> {
        if(waypoints.size() >= 1){

            if(myGeoPoint != null)
                waypoints.remove(myGeoPoint);

            myLatitude = oMapLocationOverlay.getMyLocation().getLatitude();
            myLongitude = oMapLocationOverlay.getMyLocation().getLongitude();

            myGeoPoint = new GeoPoint(myLatitude, myLongitude);
            waypoints.add(0, myGeoPoint);

            Road road = roadManager.getRoad(waypoints);

            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(map);
                nodeMarker.setPosition(node.mLocation);

                if(i == 0){
                    nodeMarker.setIcon(null);
                    nodeMarker.setTextIcon("Click on the dots to show indications");
                }else{
                    nodeMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_indications, null));
                }

                nodeMarker.setTitle("Step "+i);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
                roadMarkers.add(nodeMarker);
            }

            map.getOverlays().removeAll(roadMarkers);
            map.getOverlays().remove(roadOverlay);
            roadOverlay = RoadManager.buildRoadOverlay(road);
            map.getOverlays().add(roadOverlay);
            map.getOverlays().addAll(roadMarkers);
            roadMarkers.clear();
            map.invalidate();

        }else
            map.getOverlays().remove(roadOverlay);
        }).start();

    }

    /*******************
     * MARKER LISTENERS
     ******************/

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

        if(lastLocation != location){
            lastLocation = location;
            gpsMyLocationProvider.onLocationChanged(location);
            makeRoads();
        }
    }
}
