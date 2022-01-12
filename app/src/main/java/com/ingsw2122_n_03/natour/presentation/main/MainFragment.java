package com.ingsw2122_n_03.natour.presentation.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.databinding.FragmentMainBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.itinerary.ItineraryDetailActivity;
import com.ingsw2122_n_03.natour.presentation.support.ItineraryAdapter;

import java.util.ArrayList;

public class MainFragment extends Fragment implements ItineraryAdapter.OnItineraryListener {

    private FragmentMainBinding binding;
    private RecyclerView recyclerView;

    private ArrayList<Itinerary> itineraries = new ArrayList<>();

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            itineraries = (ArrayList<Itinerary>) bundle.getSerializable("itineraries");
        }

        recyclerView = binding.itinerary;
        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ItineraryAdapter(itineraries, this));

    }

    @Override
    public void onItineraryClick(int position) {
        Itinerary itinerary = itineraries.get(position);
        Intent intent = new Intent(getActivity(), ItineraryDetailActivity.class);
        intent.putExtra("Itinerary", itinerary);
        startActivity(intent);
    }
}