package com.ingsw2122_n_03.natour.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.model.Itinerary;

import org.joda.time.LocalTime;

import java.util.Objects;

public class AdminDialog extends AppCompatDialogFragment implements TextWatcher {

    private TextInputEditText nameEditText;
    private TextInputLayout nameLayout;

    private TextInputEditText descriptionEditText;

    private boolean isFirstSubmit = true;
    private boolean isChanged = false;

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

        nameEditText = view.findViewById(R.id.nameTextInputEditText);
        nameEditText.setText(itinerary.getName());

        nameLayout = view.findViewById(R.id.nameTextInputLayout);

        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        descriptionEditText.setText(itinerary.getDescription());

        int currentHours = itinerary.getDuration().getHourOfDay();
        int currentMinutes = itinerary.getDuration().getMinuteOfHour();

        timePicker.setHour(currentHours);
        timePicker.setMinute(currentMinutes);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.difficultyAutoComplete);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.difficulties, R.layout.difficult_list_item);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setText(arrayAdapter.getItem(itinerary.getDifficulty()).toString(), false);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChanged = true;
                if(!isFirstSubmit) isNameValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        descriptionEditText.addTextChangedListener(this);
        autoCompleteTextView.addTextChangedListener(this);

        timePicker.setOnTimeChangedListener((timePicker1, i, i1) -> isChanged = true);

        builder.setView(view)
                .setPositiveButton("Ok", (dialog, which) -> {});

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                isFirstSubmit = false;

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(button.getWindowToken(), 0);

                String name = Objects.requireNonNull(nameEditText.getText()).toString();
                String description = Objects.requireNonNull(descriptionEditText.getText()).toString();
                int difficulty = arrayAdapter.getPosition(autoCompleteTextView.getText().toString());
                int hours = timePicker.getHour();
                int minutes = timePicker.getMinute();

                if(isChanged & isNameValid()) {
                    IterController.getInstance().putItineraryByAdmin(name, description, difficulty, new LocalTime(hours, minutes));
                    dialog.dismiss();
                }else if(!isChanged){
                    dialog.dismiss();
                }
            });
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return dialog;

    }

    public boolean isNameValid(){

        if(nameEditText.getText() == null || nameEditText.getText().length() == 0) {
            nameLayout.setError(getString(R.string.name_warning));
            return false;
        }else if(nameEditText.getText().toString().matches("\\s+.*")) {
            nameLayout.setError(getString(R.string.name_space_warning));
            return false;
        }else if(nameEditText.getText().length() < 4) {
            nameLayout.setError(getString(R.string.username_length_error));
            return false;
        }

        nameLayout.setError(null);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
