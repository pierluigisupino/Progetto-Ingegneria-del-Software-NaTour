package com.ingsw2122_n_03.natour.presentation.dialogs;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;

public class DeleteDialog extends AppCompatDialogFragment {

    private boolean isFirstOpen = true;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_delete, null);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.deleteAnimation);
        lottieAnimationView.setMaxFrame(30);

        builder.setView(view).setNegativeButton(R.string.no_text, null);
        builder.setView(view).setPositiveButton(R.string.yes_text, (dialog, which) -> IterController.getInstance().deleteItinerary());

        final AlertDialog deleteDialog = builder.create();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isFirstOpen) deleteDialog.dismiss();
                isFirstOpen = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        deleteDialog.setOnShowListener(dialog -> {

            Button btnPositive = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(16);

            Typeface typefacePositive = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefacePositive);

            Button btnNegative = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(16);

            Typeface typefaceNegative = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefaceNegative);

            btnNegative.setOnClickListener(view1 -> {
                lottieAnimationView.setSpeed(-1);
                lottieAnimationView.playAnimation();
            });

        });

        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return deleteDialog;

    }

}