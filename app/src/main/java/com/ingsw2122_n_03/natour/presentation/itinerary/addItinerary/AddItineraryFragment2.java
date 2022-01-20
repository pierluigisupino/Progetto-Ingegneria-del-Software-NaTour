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

import org.joda.time.LocalTime;


public class AddItineraryFragment2 extends Fragment {

    private Fragment2AddItineraryBinding binding;

    private final AddItineraryActivity addItineraryActivity;
    private View view;
    private AutoCompleteTextView difficultyTextView;
    private ArrayAdapter<CharSequence> arrayAdapter;

    private String difficulty;
    private int hours = 1;
    private int minutes = 0;


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
        TimePicker timePicker = binding.timePicker;
        difficultyTextView = binding.difficultyAutoComplete;

        timePicker.setIs24HourView(true);
        timePicker.setHour(hours);
        timePicker.setMinute(minutes);

        timePicker.setOnTimeChangedListener((picker, newHours, newMinutes) -> {
            hours = newHours;
            minutes = newMinutes;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        difficultyTextView.setAdapter(arrayAdapter);
        if(difficulty == null) {
            difficulty = arrayAdapter.getItem(0).toString();
            difficultyTextView.setText(difficulty, false);
        }
    }


    public boolean isDurationValid(){
        if(hours !=0 || minutes !=0)
            return true;
        else{
            addItineraryActivity.onFail(getString(R.string.duration_error));
            return false;
        }
    }


    public int getDifficulty(){
        difficulty = difficultyTextView.getText().toString();
        return arrayAdapter.getPosition(difficulty);
    }


    public LocalTime getDuration(){ return new LocalTime(hours, minutes); }

}
