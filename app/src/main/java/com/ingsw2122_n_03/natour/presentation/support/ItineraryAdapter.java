package com.ingsw2122_n_03.natour.presentation.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.model.Itinerary;


import java.text.SimpleDateFormat;
import java.util.List;

public class ItineraryAdapter extends RecyclerView .Adapter<ItineraryAdapter.ItineraryViewHolder>{

    List<Itinerary> itineraries;

    public ItineraryAdapter(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItineraryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.itinerary_item,
                        parent,
                        false
                )
        );
    }


    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {

        Itinerary iter = itineraries.get(position);
        holder.setNameText(iter.getName());

        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
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


    public class ItineraryViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView dateTextView;
        TextView difficultyTextView;
        TextView durationTextView;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            difficultyTextView = itemView.findViewById(R.id.difficulty_textView);
            durationTextView = itemView.findViewById(R.id.duration_textView);
        }

        public void setNameText(String name) {
            nameTextView.setText(name);
        }

        public void setDateText(String date) {
            dateTextView.setText(date);
        }

        public void setDifficultyText(String difficulty) { difficultyTextView.setText(difficulty); }

        public void setDurationText(String duration) { durationTextView.setText(duration); }

    }
}
