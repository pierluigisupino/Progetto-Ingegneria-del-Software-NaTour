package com.ingsw2122_n_03.natour.presentation.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary.AddItineraryFragment4;

public class DistanceDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_distance, null);

        assert getArguments() != null;
        String msg = getArguments().getString("msg");

        TextView textView = view.findViewById(R.id.body);
        textView.setText(msg);

        builder.setView(view).setNegativeButton(R.string.delete_photo_text, (d, w) -> ((AddItineraryFragment4) getParentFragment()).removeInvalidPointOfInterests());
        builder.setView(view).setPositiveButton(R.string.ok_text, null);

        final AlertDialog distanceDialog = builder.create();

        distanceDialog.setOnShowListener(dialog -> {

            Button btnPositive = distanceDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(16);

            Typeface typefacePositive = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefacePositive);

            Button btnNegative = distanceDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(16);

            Typeface typefaceNegative = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefaceNegative);

        });

        distanceDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return distanceDialog;

    }

}
