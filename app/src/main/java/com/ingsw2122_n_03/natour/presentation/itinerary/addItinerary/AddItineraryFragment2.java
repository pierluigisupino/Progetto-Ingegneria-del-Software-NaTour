package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.Fragment2AddItineraryBinding;


public class AddItineraryFragment2 extends Fragment {

    private Fragment2AddItineraryBinding binding;

    private final AddItineraryActivity addItineraryActivity;
    private View view;
    private AutoCompleteTextView difficultyTextView;
    private TimePicker timePicker;
    private int hour = 1;
    private int minutes = 0;
    private String difficulty;

    public AddItineraryFragment2(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment2AddItineraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.view = view;
        timePicker = binding.timePicker;
        difficultyTextView = binding.difficultyAutoComplete;

        timePicker.setIs24HourView(true);
        timePicker.setHour(hour);
        timePicker.setMinute(minutes);

    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        difficultyTextView.setAdapter(arrayAdapter);
        if(difficulty == null) {
            difficulty = arrayAdapter.getItem(0).toString();
            difficultyTextView.setText(difficulty, false);
        }
    }

    public String getDifficulty(){
        difficulty = difficultyTextView.getText().toString();
        return difficulty;
    }

    public int getHours(){
        hour = timePicker.getHour();
        return hour;
    }

    public int getMinutes(){
        minutes = timePicker.getMinute();
        return minutes;
    }

    public boolean isDurationValid(){
        if(getHours()!=0 || getMinutes()!=0)
            return true;
        else{
            addItineraryActivity.onFail(getString(R.string.duration_error));
            return false;
        }
    }

}
