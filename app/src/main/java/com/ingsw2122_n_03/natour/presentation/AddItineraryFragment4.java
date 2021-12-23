package com.ingsw2122_n_03.natour.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.Fragment4AddItineraryBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AddItineraryFragment4 extends Fragment {

    private Fragment4AddItineraryBinding binding;
    private SearchView searchView;
    private FloatingActionButton searchButton;
    private FloatingActionButton addGPX;

    private final AddItineraryActivity addItineraryActivity;

    private MapView map = null;
    private IMapController mapController;
    private Geocoder geocoder;

    private ArrayList<GeoPoint> waypoints = new ArrayList<>();

    private ActivityResultLauncher<Intent> getGPXLauncher;


    public AddItineraryFragment4(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = Fragment4AddItineraryBinding.inflate(inflater, container, false);

        map = binding.map;
        searchView = binding.searchView;
        searchButton = binding.searchButton;
        addGPX = binding.gpxButton;

        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);

        mapController = map.getController();
        mapController.setZoom(6.5);
        mapController.animateTo(new GeoPoint(40.863, 14.2767));

        geocoder = new Geocoder(addItineraryActivity);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.e("test", query);

                List<Address> addressList = null;

                if(query != null && !query.equals("")){
                    try{
                        addressList = geocoder.getFromLocationName(query, 1);

                        if(addressList.size() > 0) {

                            for(Address a : addressList){
                                Log.e("test", String.valueOf(a));
                            }

                            Address address = addressList.get(0);
                            GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                            mapController.animateTo(geoPoint);
                            mapController.setZoom(18.0);
                        }else{
                            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.search_error));
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        getGPXLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        try {
                            InputStream is = getView().getContext().getContentResolver().openInputStream(uri);
                            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                            StringBuilder sb = new StringBuilder();
                            String readLines;
                            while((readLines = bf.readLine()) != null) {
                                sb.append(readLines+"\n");
                            }
                            is.close();
                            Log.i("READ:", sb.toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

        searchButton.setOnClickListener(view -> {
            if(searchView.getVisibility() == View.GONE){
                searchView.setVisibility(View.VISIBLE);
                searchButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close, null));
            }else{
                searchView.setVisibility(View.GONE);
                searchButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search, null));
            }
        });

        addGPX.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getGPXLauncher.launch(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OSRMRoadManager roadManager = new OSRMRoadManager(view.getContext(), null);

        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                addWaypoint(p);
                if(waypoints.size() > 1){
                    Road road = roadManager.getRoad(waypoints);
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay mEventOverlay = new MapEventsOverlay(mReceiver);
        map.getOverlays().add(mEventOverlay);

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

    private void addWaypoint(GeoPoint p) {
        Marker marker = new Marker(map);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle, null));
        map.getOverlays().add(marker);
        marker.setPosition(p);
        waypoints.add(p);
    }

}