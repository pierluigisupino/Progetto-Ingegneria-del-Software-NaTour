package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ingsw2122_n_03.natour.R;

public class AddItineraryFragment2 extends Fragment {

    private Spinner difficultySpinner;
    private TimePicker timePicker;
    private int hour = 1;
    private int minutes = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2_add_itinerary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        timePicker = view.findViewById(R.id.timePicker);
        difficultySpinner = view.findViewById(R.id.difficultyMenu);

        timePicker.setIs24HourView(true);
        timePicker.setHour(hour);
        timePicker.setMinute(minutes);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        difficultySpinner.setAdapter(arrayAdapter);

    }

    public String getDifficulty(){
        return difficultySpinner.getSelectedItem().toString();
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
        if(getHours() != 0 || getMinutes() != 0){
            return true;
        }
        Toast.makeText(getView().getContext(),"Insert a duration!!!", Toast.LENGTH_SHORT).show();
        return false;
    }

}