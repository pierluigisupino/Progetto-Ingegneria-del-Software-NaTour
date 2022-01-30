package com.ingsw2122_n_03.natour.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;

public class DeleteDialog extends AppCompatDialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_delete, null);

        builder.setView(view).setNegativeButton(R.string.no_text, null);
        builder.setView(view).setPositiveButton(R.string.yes_text, (dialog, which) -> IterController.getInstance().deleteItinerary());

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return dialog;

    }

}
