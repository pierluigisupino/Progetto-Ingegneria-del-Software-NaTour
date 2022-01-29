package com.ingsw2122_n_03.natour.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.model.Itinerary;

public class AdminDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_admin, null);

        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        assert getArguments() != null;
        Itinerary itinerary = (Itinerary) getArguments().getSerializable("itinerary");

        TextInputEditText nameTextInputEditText = view.findViewById(R.id.nameTextInputEditText);
        nameTextInputEditText.setText(itinerary.getName());

        TextInputEditText descriptionInputEditText = view.findViewById(R.id.descriptionEditText);
        descriptionInputEditText.setText(itinerary.getName());

        int currentHours = itinerary.getDuration().getHourOfDay();
        int currentMinutes = itinerary.getDuration().getMinuteOfHour();

        timePicker.setHour(currentHours);
        timePicker.setMinute(currentMinutes);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.difficultyAutoComplete);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setText(arrayAdapter.getItem(itinerary.getDifficulty()).toString(), false);

        builder.setView(view)
                .setPositiveButton("Ok", (dialog, which) -> {
                    //TODO AGGIORNARE I VALORI IN ITINERARY DETAIL CON I NUOVI VALORI
                    dialog.dismiss();
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return dialog;

    }

}
