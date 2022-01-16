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
import androidx.recyclerview.widget.GridLayoutManager;
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
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private TextView textView3;

    private ArrayList<Itinerary> itineraries = new ArrayList<>();

    private final IterController iterController = IterController.getInstance();


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

        textView1 = binding.error1;
        textView2 = binding.error2;
        imageView = binding.error3;
        textView3 = binding.error4;

        pullToRefresh = binding.update;
        recyclerView = binding.itinerary;

        Bundle bundle = getArguments();

        if(bundle != null && bundle.containsKey("itineraries"))
            itineraries = (ArrayList<Itinerary>) bundle.getSerializable("itineraries");
        else{
            recyclerView.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager =  new GridLayoutManager(getActivity(), 2);
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
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            pullToRefresh.setRefreshing(false);
        });
    }

    public void onError(){
        requireActivity().runOnUiThread(()-> {
            recyclerView.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            pullToRefresh.setRefreshing(false);
        });
    }

}