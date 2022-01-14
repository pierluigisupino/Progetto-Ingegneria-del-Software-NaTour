package com.ingsw2122_n_03.natour.presentation.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.model.Itinerary;


import java.text.SimpleDateFormat;
import java.util.List;

public class ItineraryAdapter extends RecyclerView .Adapter<ItineraryAdapter.ItineraryViewHolder>{

    public interface OnItineraryListener{
        void onItineraryClick(int position);
    }

    private final List<Itinerary> itineraries;
    private final OnItineraryListener mOnItineraryListener;
    private final Context context;

    public ItineraryAdapter(List<Itinerary> itineraries, OnItineraryListener onItineraryListener, Context context) {
        this.itineraries = itineraries;
        this.mOnItineraryListener = onItineraryListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItineraryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.itinerary_item,
                        parent,
                        false
                ),
                mOnItineraryListener
        );
    }


    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {

        Itinerary iter = itineraries.get(position);
        holder.setNameText(iter.getName());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        String shareDate = dateFormat.format(iter.getShareDate());
        holder.setDateText(shareDate);

        holder.setDifficultyText(iter.getDifficulty());

        String duration = iter.getHoursDuration() + "h & " + iter.getMinutesDuration()+"m";
        holder.setDurationText(duration);

    }


    @Override
    public int getItemCount() {
        return itineraries.size();
    }


    protected class ItineraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout difficultyLayout;
        TextView nameTextView;
        TextView dateTextView;
        ImageView difficulty1;
        ImageView difficulty2;
        ImageView difficulty3;
        ImageView difficulty4;
        TextView durationTextView;
        OnItineraryListener onItineraryListener;

        public ItineraryViewHolder(@NonNull View itemView, OnItineraryListener onItineraryListener) {
            super(itemView);
            difficultyLayout = itemView.findViewById(R.id.difficultyLayout);
            nameTextView = itemView.findViewById(R.id.name_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            difficulty1 = itemView.findViewById(R.id.difficulty1);
            difficulty2 = itemView.findViewById(R.id.difficulty2);
            difficulty3 = itemView.findViewById(R.id.difficulty3);
            difficulty4 = itemView.findViewById(R.id.difficulty4);
            durationTextView = itemView.findViewById(R.id.duration_textView);
            this.onItineraryListener = onItineraryListener;

            itemView.setOnClickListener(this);
        }

        public void setNameText(String name) {
            nameTextView.setText(name);
        }

        public void setDateText(String date) {
            dateTextView.setText(date);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setDifficultyText(String difficulty) {
            if(difficulty.equals(context.getString(R.string.difficulty1))) {
                difficulty1.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
            }else if(difficulty.equals(context.getString(R.string.difficulty2))) {
                difficulty1.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty2.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if(difficulty.equals(context.getString(R.string.difficulty3))) {
                difficulty1.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty2.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty3.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
            }else if(difficulty.equals(context.getString(R.string.difficulty4))) {
                difficulty1.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty2.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty3.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
                difficulty4.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            difficultyLayout.setOnTouchListener((view, motionEvent) -> {
                Toast.makeText(view.getContext(), difficulty, Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        public void setDurationText(String duration) { durationTextView.setText(duration); }

        @Override
        public void onClick(View view) {
            onItineraryListener.onItineraryClick(getAdapterPosition());
        }
    }
}
