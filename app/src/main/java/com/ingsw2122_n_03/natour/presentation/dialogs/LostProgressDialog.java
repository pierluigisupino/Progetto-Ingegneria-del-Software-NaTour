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

public class LostProgressDialog extends AppCompatDialogFragment {

    private Button btnPositive;
    private Button btnNegative;

    private boolean wantsToDismiss = false;
    private boolean wantsToClose = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_lost_progress, null);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.deleteAnimation);
        lottieAnimationView.setFrame(150);

        builder.setView(view).setNegativeButton(R.string.no_text, null);
        builder.setView(view).setPositiveButton(R.string.yes_text, null);

        final AlertDialog deleteDialog = builder.create();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                btnNegative.setClickable(true);
                btnPositive.setClickable(true);

                if(wantsToClose) {
                    deleteDialog.dismiss();
                    requireActivity().finish();
                }else if(wantsToDismiss){
                    deleteDialog.dismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        deleteDialog.setOnShowListener(dialog -> {

            btnPositive = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(16);

            Typeface typefacePositive = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefacePositive);

            btnNegative = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(16);

            Typeface typefaceNegative = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefaceNegative);

            btnNegative.setOnClickListener(view1 -> {
                btnNegative.setClickable(false);
                btnPositive.setClickable(false);
                lottieAnimationView.setMaxFrame(150);
                lottieAnimationView.setFrame(150);
                lottieAnimationView.setSpeed(-1);
                lottieAnimationView.playAnimation();
                wantsToDismiss = true;
            });

            btnPositive.setOnClickListener(view1 -> {
                btnNegative.setClickable(false);
                btnPositive.setClickable(false);
                lottieAnimationView.setMinAndMaxFrame(150, 270);
                lottieAnimationView.playAnimation();
                wantsToClose = true;
            });

        });

        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return deleteDialog;

    }

}