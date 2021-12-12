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
import android.widget.Toast;

import com.ingsw2122_n_03.natour.R;

public class AddItineraryFragment2 extends Fragment {

    private View view;
    private AutoCompleteTextView difficultyTextView;
    private TimePicker timePicker;
    private int hour = 1;
    private int minutes = 0;
    private String difficulty;

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

        this.view = view;
        timePicker = view.findViewById(R.id.timePicker);
        difficultyTextView = view.findViewById(R.id.difficultyAutoComplete);

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
            Toast.makeText(view.getContext(), "Choose a Duration!!!!!!!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
