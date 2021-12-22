package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.BuildConfig;
import com.ingsw2122_n_03.natour.databinding.Fragment4AddItineraryBinding;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.directions.FetchURL;
import com.ingsw2122_n_03.natour.infastructure.directions.TaskLoadedCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AddItineraryFragment4 extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private Fragment4AddItineraryBinding binding;
    private FloatingActionButton searchButton;
    private FloatingActionButton addGPX;
    private AutocompleteSupportFragment autocompleteFragment;

    private ActivityResultLauncher<Intent> getGPX;
    private AddItineraryActivity addItineraryActivity;

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private Geocoder geocoder;

    private List<Marker> markers = new LinkedList<>();
    private Polyline polyline;

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

        searchButton = binding.searchButton;

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        Places.initialize(getActivity(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getActivity());

        geocoder = new Geocoder(getActivity());

        setupAutoCompleteFragment();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View root = autocompleteFragment.getView();
                root.post(new Runnable() {
                    @Override
                    public void run() {
                        root.findViewById(R.id.places_autocomplete_search_input)
                                .performClick();
                    }
                });
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupAutoCompleteFragment() {

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15);
                mMap.animateCamera(location);
            }

            @Override
            public void onError(@NonNull Status status) {

                if(!status.toString().contains("CANCELED")){
                    addItineraryActivity.onFail(getString(R.string.generic_error));
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        int vectorResId = R.drawable.ic_circle_finish;

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);

                if(markers.size() == 1){
                    Marker marker = markers.get(markers.size() - 1);
                    marker.setIcon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_circle_start));
                    vectorResId = R.drawable.ic_circle_finish;
                }else if(markers.size() > 1){
                    Marker marker = markers.get(markers.size() - 1);
                    marker.setIcon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_circle));
                    vectorResId = R.drawable.ic_circle_finish;
                }

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(null)
                        .draggable(true)
                        .icon(bitmapDescriptorFromVector(getActivity(), vectorResId))
                );

                markers.add(marker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        int index = markers.indexOf(marker);

        if(index == 0 && markers.size() > 1){
            markers.get(index + 1).setIcon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_circle_start));
        } else if(index == markers.size() - 1 && markers.size() > 1){
            markers.get(index - 1).setIcon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_circle_finish));
        }

        marker.remove();
        markers.remove(index);
        return true;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Marker getStartMarker(){
        return markers.get(0);
    }

    public Marker getEndMarker(){
        return markers.get(markers.size() - 1);
    }

    public LatLng[] getWaypoints(){
        List<Marker> markers = new LinkedList<>();
        markers.addAll(this.markers);

        markers.remove(0);
        markers.remove(markers.size() - 1);

        LatLng[] waypoints = new LatLng[markers.size()];

        for(int i = 0; i < markers.size(); i++){
            waypoints[i] = markers.get(i).getPosition();
        }

        return waypoints;
    }

    public GoogleMap getMap(){
        return this.mMap;
    }
}