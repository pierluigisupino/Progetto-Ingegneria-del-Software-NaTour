package com.ingsw2122_n_03.natour.presentation.main;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.FragmentMainBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.support.ItineraryAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class MainFragment extends Fragment implements ItineraryAdapter.OnItineraryListener {

    private FragmentMainBinding binding;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private Parcelable recyclerViewState;

    private TextView textViewError1;
    private TextView textViewError2;
    private ImageView imageViewError;
    private TextView textViewError3;

    private ArrayList<Itinerary> itineraries = new ArrayList<>();

    private final IterController iterController = IterController.getInstance();
    private Bundle bundle;


    public MainFragment(){
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

        textViewError1 = binding.error1;
        textViewError2 = binding.error2;
        imageViewError = binding.error3;
        textViewError3 = binding.error4;

        pullToRefresh = binding.update;
        recyclerView = binding.itinerary;

        bundle = getArguments();

        if(bundle != null && bundle.containsKey("itineraries"))
            itineraries = (ArrayList<Itinerary>) bundle.getSerializable("itineraries");
        else{
            recyclerView.setVisibility(View.GONE);
            textViewError1.setVisibility(View.VISIBLE);
            textViewError2.setVisibility(View.VISIBLE);
            imageViewError.setVisibility(View.VISIBLE);
            textViewError3.setVisibility(View.VISIBLE);
        }


        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new ItineraryAdapter(itineraries, this, getContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
                if(newState==RecyclerView.SCROLL_STATE_IDLE && (layoutManager.findLastCompletelyVisibleItemPosition() == Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() - 1)) {
                    iterController.retrieveItineraries();
                }
            }

        });

        pullToRefresh.setOnRefreshListener(iterController::updateItineraries);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(bundle != null && bundle.containsKey("itineraries"))
            bundle.putSerializable("itineraries", itineraries);
    }

    public void updateItineraries(ArrayList<Itinerary> itineraries) {
        this.itineraries = itineraries;
        requireActivity().runOnUiThread(()->{
            recyclerView.setAdapter(new ItineraryAdapter(itineraries, this, getContext()));
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);
        });
    }


    @Override
    public void onItineraryClick(int position) {
        iterController.onItineraryClick(itineraries.get(position));
    }

    
    public void onSuccess(){
        requireActivity().runOnUiThread(()-> {
            recyclerView.setVisibility(View.VISIBLE);
            textViewError1.setVisibility(View.GONE);
            textViewError2.setVisibility(View.GONE);
            imageViewError.setVisibility(View.GONE);
            textViewError3.setVisibility(View.GONE);
            pullToRefresh.setRefreshing(false);
        });
    }


    public void onError(){
        requireActivity().runOnUiThread(()-> {
            recyclerView.setVisibility(View.GONE);
            textViewError1.setVisibility(View.VISIBLE);
            textViewError2.setVisibility(View.VISIBLE);
            imageViewError.setVisibility(View.VISIBLE);
            textViewError3.setVisibility(View.VISIBLE);
            pullToRefresh.setRefreshing(false);
        });
    }

}