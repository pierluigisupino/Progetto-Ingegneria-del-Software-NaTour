package com.ingsw2122_n_03.natour.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.LatLng;
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

import java.util.Arrays;

public class AddItineraryFragment4 extends Fragment implements OnMapReadyCallback {

    private Fragment4AddItineraryBinding binding;
    private FloatingActionButton searchButton;
    private FloatingActionButton addGPX;
    private AutocompleteSupportFragment autocompleteFragment;

    private ActivityResultLauncher<Intent> getGPX;
    private AddItineraryActivity addItineraryActivity;

    private GoogleMap mMap;
    private PlacesClient placesClient;

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
    }
}