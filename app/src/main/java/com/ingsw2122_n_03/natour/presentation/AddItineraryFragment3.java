package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ingsw2122_n_03.natour.R;

public class AddItineraryFragment3 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private String name;
    private String description;
    private String difficulty;
    private String hours;
    private String minutes;

    public AddItineraryFragment3() {}

    public static AddItineraryFragment3 newInstance(String name, String description, String difficulty, int hours, int minutes) {
        AddItineraryFragment3 fragment = new AddItineraryFragment3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, difficulty);
        args.putString(ARG_PARAM4, String.valueOf(hours));
        args.putString(ARG_PARAM5, String.valueOf(minutes));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            description = getArguments().getString(ARG_PARAM2);
            difficulty = getArguments().getString(ARG_PARAM3);
            hours = getArguments().getString(ARG_PARAM4);
            minutes = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3_add_itinerary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("test", name + " " +  description + " " + difficulty + " " + hours + " " + minutes);
    }
}