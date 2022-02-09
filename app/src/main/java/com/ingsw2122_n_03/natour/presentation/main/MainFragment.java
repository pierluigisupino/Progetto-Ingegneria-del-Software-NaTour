package com.ingsw2122_n_03.natour.presentation.main;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.FragmentMainBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.support.ItineraryAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class MainFragment extends Fragment implements ItineraryAdapter.OnItineraryListener {

    private FragmentMainBinding binding;
    private LinearLayout mainLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private Parcelable recyclerViewState;

    private TextView textViewError1;
    private LottieAnimationView errorAnimation;
    private LottieAnimationView swipeAnimation;
    private TextView textViewError3;

    private boolean onError = false;
    private boolean shouldUpdate = false;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        textViewError1 = binding.error1;
        textViewError3 = binding.error4;

        pullToRefresh = binding.update;
        recyclerView = binding.itinerary;

        mainLayout = binding.mainLayout;

        errorAnimation = binding.errorAnimation;
        errorAnimation.setMaxFrame(70);

        swipeAnimation = binding.swipeAnimation;

        bundle = getArguments();
        if(bundle == null || !bundle.containsKey("itineraries"))
            onError = true;

        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

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

        pullToRefresh.setOnRefreshListener(() -> {
            iterController.updateItineraries();
            shouldUpdate = false;
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }


    @SuppressWarnings("unchecked")
    private void updateUi() {

        requireActivity().runOnUiThread(()-> {

            if(shouldUpdate) {
                swipeAnimation.setVisibility(View.VISIBLE);
            }else {
                swipeAnimation.setVisibility(View.GONE);
            }

            pullToRefresh.setRefreshing(false);

            if(onError) {
                mainLayout.setVisibility(View.GONE);
                textViewError1.setVisibility(View.VISIBLE);
                errorAnimation.setVisibility(View.VISIBLE);
                textViewError3.setVisibility(View.VISIBLE);
                return;
            }

            mainLayout.setVisibility(View.VISIBLE);
            textViewError1.setVisibility(View.GONE);
            errorAnimation.setVisibility(View.GONE);
            textViewError3.setVisibility(View.GONE);
            recyclerView.setAdapter(new ItineraryAdapter((ArrayList<Itinerary>) bundle.getSerializable("itineraries"), this, getContext()));
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);

        });

    }

    public void showUpdateHint(){
        shouldUpdate = true;
        if(this.isVisible()) updateUi();
    }

    public void updateItineraries(ArrayList<Itinerary> itineraries) {
        onError = false;
        bundle.putSerializable("itineraries", itineraries);
        if(this.isVisible()) updateUi();
    }


    public void onError() {
        onError = true;
        if(this.isVisible()) updateUi();
    }
    

    @Override
    public void onItineraryClick(int position) {
        iterController.onItineraryClick(position);
    }

}