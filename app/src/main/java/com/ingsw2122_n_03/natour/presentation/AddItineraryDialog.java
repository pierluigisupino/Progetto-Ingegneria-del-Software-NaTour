package com.ingsw2122_n_03.natour.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ingsw2122_n_03.natour.R;

public class AddItineraryDialog extends AppCompatDialogFragment {


    AutoCompleteTextView autoCompleteTextView;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_itinerary, null);

        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);




        autoCompleteTextView = view.findViewById(R.id.difficultyAutoComplete);

        String[] difficulties = new String[]{
                "diff 1",
                "diff 2",
                "diff 3",
        };

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.difficult_list_item, difficulties);

        //autoCompleteTextView.setAdapter(arrayAdapter);

        //autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString(), false);


        builder.setView(view)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
