package com.ingsw2122_n_03.natour.presentation.itinerary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.ActivityFollowItineraryBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.support.NaTourMarker;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
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

public class FollowItineraryActivity extends AppCompatActivity {

    private ActivityFollowItineraryBinding binding;

    private Itinerary itinerary;
    private MapView map;
    private IMapController mapController;
    private RoadManager roadManager;
    private Polyline roadOverlay;
    private MyLocationNewOverlay oMapLocationOverlay;
    private double myLatitude;
    private double myLongitude;
    private GeoPoint myGeoPoint;

    private final ArrayList<Marker> markers = new ArrayList<>();
    private final ArrayList<GeoPoint> waypoints = new ArrayList<>();

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = ActivityFollowItineraryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("itinerary");

        checkPermissionsState();
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

    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE);

        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {

            setupMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE
                    },
                    MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean somePermissionWasDenied = false;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        somePermissionWasDenied = true;
                        break;
                    }
                }
                if (somePermissionWasDenied) {
                    Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    setupMap();
                }
            } else {
                Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupMap() {

        map = binding.map;
        map.setClickable(true);
        map.setMultiTouchControls(true);

        map.getController().setZoom(15.0);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        roadManager = new OSRMRoadManager(this, null);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        oMapLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        map.getOverlays().add(oMapLocationOverlay);
        oMapLocationOverlay.enableFollowLocation();
        oMapLocationOverlay.enableMyLocation();
        oMapLocationOverlay.enableFollowLocation();

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        addItinerary();
    }


    private void addItinerary(){
        for(WayPoint wayPoint : itinerary.getWayPoints()){

            NaTourMarker marker = new NaTourMarker(map);

            if(markers.size() == 0) {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            }else if(markers.size() == 1) {
                markers.get(0).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            }else{
                markers.get(markers.size() - 1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle, null));
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            }

            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setPosition(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));

            map.getOverlays().add(marker);
            map.invalidate();
            markers.add(marker);

            NaTourMarker.NaTourGeoPoint naTourWaypoint = marker.new NaTourGeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude());
            waypoints.add(naTourWaypoint);
        }

        makeRoads();
    }

    private void makeRoads(){

        new Thread(()-> {
            if(waypoints.size() >= 1){

                while(oMapLocationOverlay.getMyLocation() == null){

                }

                myLatitude = oMapLocationOverlay.getMyLocation().getLatitude();
                myLongitude = oMapLocationOverlay.getMyLocation().getLongitude();

                myGeoPoint = new GeoPoint(myLatitude, myLongitude);
                waypoints.add(0, myGeoPoint);

                Road road = roadManager.getRoad(waypoints);
                map.getOverlays().remove(roadOverlay);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();
            }else {
                map.getOverlays().remove(roadOverlay);
            }
        }).start();
    }
}