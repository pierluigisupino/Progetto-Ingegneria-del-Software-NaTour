package com.ingsw2122_n_03.natour.presentation.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.FragmentMainBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.support.ItineraryAdapter;

import java.util.ArrayList;

public class MainFragment extends Fragment implements ItineraryAdapter.OnItineraryListener {


    private FragmentMainBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Itinerary> itineraries = new ArrayList<>();

    private final IterController iterController;
    private final MainActivity mainActivity;


    public MainFragment(MainActivity mainActivity, IterController controller) {
        this.mainActivity = mainActivity;
        iterController = controller;
        iterController.setMainFragment(this);
    }

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

        SwipeRefreshLayout pullToRefresh = binding.update;

        recyclerView = binding.itinerary;
        LinearLayoutManager layoutManager =  new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ItineraryAdapter(itineraries, this, getContext()));

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItineraries();
                mainActivity.onSuccess("Updated");
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    public void updateItineraries(ArrayList<Itinerary> itineraries) {
        this.itineraries = itineraries;
        recyclerView.setAdapter(new ItineraryAdapter(itineraries, this, getContext()));
    }

    @Override
    public void onItineraryClick(int position) {
        iterController.onItineraryClick(itineraries.get(position));
    }

    public void getItineraries(){
        itineraries = iterController.getItineraries();
    }

}