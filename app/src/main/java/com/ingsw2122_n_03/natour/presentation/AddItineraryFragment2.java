package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import com.ingsw2122_n_03.natour.R;

public class AddItineraryFragment2 extends Fragment {

    private AutoCompleteTextView difficultyAutoCompleteTextView;
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
        difficultyAutoCompleteTextView = view.findViewById(R.id.difficultyAutoComplete);

        timePicker.setIs24HourView(true);
        timePicker.setHour(hour);
        timePicker.setMinute(minutes);

        String[] difficulties = new String[]{
                "Turistico",
                "Escursionistico",
                "Esperto"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.difficult_list_item, difficulties);
        difficultyAutoCompleteTextView.setAdapter(arrayAdapter);
        difficultyAutoCompleteTextView.setText(difficultyAutoCompleteTextView.getAdapter().getItem(0).toString(), false);
    }

    public String getDifficulty(){
        return difficultyAutoCompleteTextView.getText().toString();
    }

    public int getHours(){
        hour = timePicker.getHour();
        return hour;
    }

    public int getMinutes(){
        minutes = timePicker.getMinute();
        return minutes;
    }

}