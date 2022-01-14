package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.ingsw2122_n_03.natour.presentation.support.NaTourMarker;
import com.ingsw2122_n_03.natour.presentation.support.PointOfInterest;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;

public class AddItineraryFragment4 extends Fragment implements Marker.OnMarkerClickListener, Marker.OnMarkerDragListener {

    private SearchView searchView;
    private FloatingActionButton searchButton;

    private final AddItineraryActivity addItineraryActivity;

    private MapView map;
    private IMapController mapController;
    private Geocoder geocoder;
    private RoadManager roadManager;
    private Polyline roadOverlay;

    private final ArrayList<Marker> markers = new ArrayList<>();
    private final ArrayList<GeoPoint> waypoints = new ArrayList<>();
    private final ArrayList<PointOfInterest> pointOfInterests = new ArrayList<>();
    private ArrayList<byte[]> imagesBytes;
    private HashMap<byte[], GeoPoint> rawPointOfInterests;

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

       Fragment4AddItineraryBinding binding = Fragment4AddItineraryBinding.inflate(inflater, container, false);

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
                            mapController.setZoom(15.50);
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
                        Uri uri = data.getData();
                        Gpx parsedGpx;

                        InputStream is = getInputStream(uri);
                        parsedGpx = parsGpx(is);

                        if (parsedGpx != null) {
                            clearMap();
                            addGpxWaypoints(parsedGpx);
                            makeRoads();
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getGPXLauncher.launch(intent);
        });

        pointOfInterests.clear();

        for(Map.Entry<byte[], GeoPoint> entry : rawPointOfInterests.entrySet()){
            PointOfInterest pointOfInterest = new PointOfInterest(map, entry.getKey());
            pointOfInterest.setPosition(entry.getValue());
            this.pointOfInterests.add(pointOfInterest);
        }

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

        addPointOfInterests();

    }

    @Override
    public void onResume() {
        super.onResume();

        for(Marker marker : markers){

            if(markers.indexOf(marker) == 0){
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            }else if(markers.indexOf(marker) == markers.size() - 1){
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            }else{
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle, null));
            }

            map.getOverlays().add(marker);
        }
        makeRoads();

        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    /************
     * MAP UTILS
     ************/

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
        map.invalidate();
        markers.add(marker);

        NaTourMarker.NaTourGeoPoint naTourWaypoint = marker.new NaTourGeoPoint(p.getLatitude(), p.getLongitude());
        waypoints.add(naTourWaypoint);

    }

    private void addPointOfInterests(){

        for (PointOfInterest p : pointOfInterests) {

            byte[] bytes = p.getBytes();
            GeoPoint geoPoint = p.getPosition();

            BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            p.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image, null));
            p.setImage(drawable);

            p.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            p.setPosition(geoPoint);

            p.setOnMarkerClickListener(this);

            map.getOverlays().add(p);

        }
    }

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
            requireView().post(addItineraryActivity::hideProgressBar);
        }).start();
    }

    private void clearMap(){
        for(Marker marker : markers){
            waypoints.remove(((NaTourMarker) marker).getGeoPoint());
            map.getOverlays().remove(marker);
        }
        map.getOverlays().remove(roadOverlay);
        markers.clear();
    }

    /*******************
     * MARKER LISTENERS
     ******************/

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        if(marker instanceof PointOfInterest)
            showImage(marker.getImage());
        else{

            int index = markers.indexOf(marker);

            if (markers.size() > 1 && index == 0) {
                markers.get(index + 1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            } else if (index == markers.size() - 1 && markers.size() > 2) {
                markers.get(index - 1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            }

            waypoints.remove(((NaTourMarker) marker).getGeoPoint());
            map.getOverlays().remove(marker);
            markers.remove(index);
            makeRoads();

        }
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

    public void showImage(Drawable drawable) {

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        builder.setOnDismissListener(dialogInterface -> {

        });

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageDrawable(drawable);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();

    }

    /************
     * GPX UTILS
     ***********/

    private InputStream getInputStream(Uri uri){

        InputStream is = null;

        try {
            is = requireView().getContext().getContentResolver().openInputStream(uri);
        } catch (IOException e) {
            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.generic_error));
        }

        return is;
    }

    private Gpx parsGpx(InputStream inputStream){

        if(inputStream == null){
            return null;
        }

        GPXParser mParser = new GPXParser();
        Gpx parsedGpx = null;

        try {
            parsedGpx = mParser.parse(inputStream);
        } catch (IOException | XmlPullParserException e) {
            addItineraryActivity.onFail(addItineraryActivity.getResources().getString(R.string.generic_error));
        }

        return parsedGpx;
    }

    private void addGpxWaypoints(Gpx gpx){

        List<WayPoint> wayPoints = gpx.getWayPoints();
        List<Track> tracks = gpx.getTracks();

        if(wayPoints.size() > 0){
            WayPoint startWayPoint = wayPoints.get(0);
            addWaypoint(new GeoPoint(startWayPoint.getLatitude(), startWayPoint.getLongitude()));
        }

        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            List<TrackSegment> segments = track.getTrackSegments();
            for (int j = 0; j < segments.size(); j++) {
                TrackSegment segment = segments.get(j);

                for (TrackPoint trackPoint : segment.getTrackPoints()) {
                    addWaypoint(new GeoPoint(trackPoint.getLatitude(), trackPoint.getLongitude()));
                }
            }

            if (wayPoints.size() == 2) {
                WayPoint endWayPoint = wayPoints.get(wayPoints.size() - 1);
                addWaypoint(new GeoPoint(endWayPoint.getLatitude(), endWayPoint.getLongitude()));
            }
        }
    }

    /*****************
     * ACTIVITY UTILS
     ****************/

    private void showErrorDialog(String msg, ArrayList<PointOfInterest> invalidPointOfInterests){

        new AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.ic_error)
                .setTitle(requireContext().getString(R.string.warning_text))
                .setMessage(msg)
                .setNegativeButton(requireContext().getString(R.string.delete_photo_text), (dialogInterface, i) -> {
                    for(PointOfInterest p : invalidPointOfInterests) {
                        imagesBytes.remove(p.getBytes());
                        pointOfInterests.remove(p);
                        map.getOverlays().remove(p);
                        map.invalidate();
                    }

                })
                .setPositiveButton(requireContext().getString(R.string.ok_text), null)
                .show();
    }

    public boolean isStartPointInserted() {
        if(!waypoints.isEmpty())
            return true;
        else{
            addItineraryActivity.onFail(getString(R.string.start_point_error));
            return false;
        }
    }

    public boolean arePositionsCorrect() {

        boolean ret = true;
        ArrayList<PointOfInterest> invalidPointOfInterests = new ArrayList<>();

        if(waypoints.size() > 1) {

            for (PointOfInterest p : pointOfInterests) {

                if(!roadOverlay.isCloseTo(p.getPosition(), 100, map)) {
                    invalidPointOfInterests.add(p);
                    ret = false;
                }
            }

            if(!ret) {
                showErrorDialog(requireContext().getString(R.string.position_error), invalidPointOfInterests);
            }

        }else {

            for (PointOfInterest p : pointOfInterests) {

                if (p.getPosition().distanceToAsDouble(waypoints.get(0)) > 10000) {
                    invalidPointOfInterests.add(p);
                    ret = false;
                }
            }

            if(!ret) {
                showErrorDialog(requireContext().getString(R.string.position_error_point), invalidPointOfInterests);
            }

        }

        return ret;

    }


    public void setRawPointOfInterests(HashMap<byte[], GeoPoint> poi) {
        rawPointOfInterests = poi;
    }

    public void setImageBytes(ArrayList<byte[]> imagesBytes) {
        this.imagesBytes = imagesBytes;
    }


    public ArrayList<GeoPoint> getWaypoints(){
        return waypoints;
    }

}