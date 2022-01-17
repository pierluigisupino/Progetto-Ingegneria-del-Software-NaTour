package com.ingsw2122_n_03.natour.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.model.Itinerary;

public class FeedBackDialog extends AppCompatDialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_feedback, null);

        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        assert getArguments() != null;
        Itinerary itinerary = (Itinerary) getArguments().getSerializable("itinerary");

        int currentHours = itinerary.getDuration().getHourOfDay();
        int currentMinutes = itinerary.getDuration().getMinuteOfHour();

        timePicker.setHour(currentHours);
        timePicker.setMinute(currentMinutes);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.difficultyAutoComplete);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setText(itinerary.getDifficulty(), false);


        builder.setView(view)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dismiss())
                .setPositiveButton(getString(R.string.continue_button_text), (dialog, which) -> {
                    int hours = timePicker.getHour();
                    int minutes = timePicker.getMinute();
                    if(hours !=0 || minutes !=0)
                        IterController.getInstance().manageFeedback((hours*60)+minutes, autoCompleteTextView.getText().toString());
                    else
                        Toast.makeText(requireContext(), getString(R.string.duration_error), Toast.LENGTH_SHORT).show();

                });


        return builder.create();

    }
}
