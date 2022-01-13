package com.ingsw2122_n_03.natour.presentation.itinerary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.ActivityFollowItineraryBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.support.NaTourMarker;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowItineraryActivity extends AppCompatActivity {

    private Itinerary itinerary;
    private MapView map;
    private IMapController mapController;

    private final ArrayList<Marker> markers = new ArrayList<>();
    private final ArrayList<GeoPoint> waypoints = new ArrayList<>();

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private MyLocationNewOverlay myLocationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        ActivityFollowItineraryBinding binding = ActivityFollowItineraryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("itinerary");

        map = binding.map;

        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);

        addItinerary();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
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
    }
}