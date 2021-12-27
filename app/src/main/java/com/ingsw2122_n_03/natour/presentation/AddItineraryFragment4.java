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
import com.ingsw2122_n_03.natour.model.NaTourMarker;

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
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;

public class AddItineraryFragment4 extends Fragment implements Marker.OnMarkerClickListener, Marker.OnMarkerDragListener {

    private SearchView searchView;
    private FloatingActionButton searchButton;

    private final AddItineraryActivity addItineraryActivity;

    private MapView map;
    private IMapController mapController;
    private Geocoder geocoder;
    private RoadManager roadManager;
    private Polyline roadOverlay;

    private ArrayList<Marker> markers = new ArrayList<>();
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

        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        com.ingsw2122_n_03.natour.databinding.Fragment4AddItineraryBinding binding = Fragment4AddItineraryBinding.inflate(inflater, container, false);

        map = binding.map;
        searchView = binding.searchView;
        searchButton = binding.searchButton;
        FloatingActionButton addGPX = binding.gpxButton;

        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);

        mapController = map.getController();
        mapController.setZoom(6.5);
        mapController.animateTo(new GeoPoint(40.863, 14.2767));

        geocoder = new Geocoder(addItineraryActivity);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                List<Address> addressList;

                if(query != null && !query.equals("")){
                    try{
                        addressList = geocoder.getFromLocationName(query, 1);

                        if(addressList.size() > 0) {
                            Address address = addressList.get(0);
                            GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                            mapController.animateTo(geoPoint);
                            mapController.setZoom(18.0);
                        }else{
                            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.search_error));
                        }

                    }catch (IOException e){
                        addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.generic_error));
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
                        GPXParser mParser = new GPXParser();
                        Gpx parsedGpx = null;
                        Uri uri = data.getData();
                        try {
                            InputStream is = requireView().getContext().getContentResolver().openInputStream(uri);
                            parsedGpx = mParser.parse(is);

                        } catch (IOException | XmlPullParserException e) {
                            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.generic_error));
                        }

                        if (parsedGpx != null) {

                            // TODO: 27/12/2021

                            //I FILE GPX HANNO TUTTI ALMENO UN TRACK AL LORO INTERNO? parsedGpx.getTracks();
                            //SE HANNO SOLO PUNTO INIZIALE E FINALE?  parsedGpx.getWayPoints();
                            //COSA FA parsedGpx.getRoutes(); ?

                            List<Track> tracks = parsedGpx.getTracks();
                            for (int i = 0; i < tracks.size(); i++) {
                                Track track = tracks.get(i);
                                List<TrackSegment> segments = track.getTrackSegments();
                                for (int j = 0; j < segments.size(); j++) {
                                    TrackSegment segment = segments.get(j);

                                    for (TrackPoint trackPoint : segment.getTrackPoints()) {
                                        addWaypoint(new GeoPoint(trackPoint.getLatitude(), trackPoint.getLongitude()));
                                    }
                                }

                                makeRoads();
                            }
                        } else {
                            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.generic_error));
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
            for(Marker marker : markers){
                waypoints.remove(((NaTourMarker) marker).getGeoPoint());
                map.getOverlays().remove(marker);
            }
            map.getOverlays().remove(roadOverlay);
            markers.clear();
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

        roadManager = new OSRMRoadManager(view.getContext(), null);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                addWaypoint(p);
                makeRoads();
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

        marker.setDraggable(true);
        marker.setOnMarkerClickListener(this);
        marker.setOnMarkerDragListener(this);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setPosition(p);

        map.getOverlays().add(marker);
        markers.add(marker);

        NaTourMarker.NaTourGeoPoint naTourWaypoint = marker.new NaTourGeoPoint(p.getLatitude(), p.getLongitude());
        waypoints.add(naTourWaypoint);
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        int index = markers.indexOf(marker);

        if(markers.size() > 1 && index == 0){
            markers.get(index + 1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
        } else if(index == markers.size() - 1 && markers.size() > 1){
            markers.get(index - 1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
        }

        waypoints.remove(((NaTourMarker) marker).getGeoPoint());
        map.getOverlays().remove(marker);
        markers.remove(index);
        makeRoads();
        return true;
    }

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {

        ((NaTourMarker) marker).getGeoPoint().setLatitude(marker.getPosition().getLatitude());
        ((NaTourMarker) marker).getGeoPoint().setLongitude(marker.getPosition().getLongitude());
        makeRoads();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    private void makeRoads(){

        addItineraryActivity.showProgressBar();
        new Thread(()-> {
            if(waypoints.size() > 1){
                Road road = roadManager.getRoad(waypoints);
                map.getOverlays().remove(roadOverlay);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();
            }else {
                map.getOverlays().remove(roadOverlay);
            }
            requireView().post(()->addItineraryActivity.hideProgressBar());
        }).start();

    }

}