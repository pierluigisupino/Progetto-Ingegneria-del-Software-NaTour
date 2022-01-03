package com.ingsw2122_n_03.natour.presentation.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.model.Itinerary;

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
        holder.setCreatorText(iter.getCreator().getName());
    }


    @Override
    public int getItemCount() {
        return itineraries.size();
    }


    public class ItineraryViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView creatorTextView;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_textView);
            creatorTextView = itemView.findViewById(R.id.creator_textView);
        }

        public void setNameText(String name) {
            nameTextView.setText(name);
        }

        public void setCreatorText(String creator) {
            creatorTextView.setText(creator);
        }

    }
}
